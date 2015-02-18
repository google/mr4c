#include "algo_dev_api.h"
#include "J2kDecoder.h"
#include <log4cxx/logger.h>
#include <set>
#include <iostream>
#include <sstream>
#include <string>
#include <cstdio>
#include <cstring>

using namespace MR4C;

using log4cxx::LoggerPtr;
// the bit-depth of the input files
#define INPUT_BIT_DEPTH  11

class Histograms : public Algorithm {
    /** inputs:  directory : images
     *                       Contains j2k files of which to compute histograms
     *  outputs: directory : histograms
     *                       Location to place computed histogram files
     *           directory  :intermediate_avg_pixels
     *                       Location to place the intermediate
     *                       pixel files
     *  params:  integer   : num_buckets
     *                       The number of buckets to use for the histogram
     *                      
     */


    typedef std::set<DataKey> DataKeySet;

    DataKeyDimension m_nameDimension;
    int              m_numBuckets;
    double *         m_averageValues;
    int              m_totalImages;
    int              m_numPixels;
    J2kImageDecoder  m_imageDecoder;

public:
    Histograms() : m_nameDimension( "NAME" ), m_averageValues( 0 ), m_totalImages( 0 ) {
    }
    
    static Algorithm* create() {
        /**
         * Instantiate an instance of the algortihm and add the input
         * and output datasets
         */
        Histograms* algo = new Histograms();
        algo->addInputDataset("images");
        algo->addOutputDataset("histograms");
        algo->addOutputDataset("intermediate_avg_pixels");
        return algo;
    }

    
    void computeHistogram( char * data, size_t len, DataKeyElement & inName, Dataset * outputDataset ) {
        /**
         * Given a J2K data stream and its length, computes the
         * histogram.  Uses inName when adding the resulting histogram
         * to the outputDataset
         */
        
        // Assumes images are 11-bit Gray images
        // This could be parameterized in the algorithm or determined
        // dynamically by functions from libjpeg
        J2kImage * image = m_imageDecoder.getImage( data, len );
        J2kImage::DimType dim = image->getDimensions();
        int num_pixels = dim.first * dim.second;

        // If this is the first time through, create the average
        // values array
        if ( m_averageValues == 0 ) {
            m_averageValues = new double[ num_pixels ];
            memset( m_averageValues, 0, sizeof( double ) * num_pixels );
            m_totalImages = 0;
            m_numPixels = num_pixels;
        }

        int * pic_data = image->getPixels();

        // Initialize the histogram
        int histogram[ m_numBuckets ];
        memset( histogram, 0, m_numBuckets * sizeof( int ));
        const int histo_div = (( 1 << INPUT_BIT_DEPTH ) + ( m_numBuckets - 1 )) / 
            m_numBuckets;

        // iterate over each pixel, incrementing the appropriate
        // histogram bucket and computing the incremental average
        // pixel value
        for ( int cur_pixel = 0; cur_pixel < num_pixels; cur_pixel++ ) {
            histogram[ pic_data[ cur_pixel ] / histo_div ]++;
            double cur_value = m_averageValues[ cur_pixel ] * m_totalImages;
            cur_value += pic_data[ cur_pixel ];
            cur_value /= ( m_totalImages + 1 );
            m_averageValues[ cur_pixel ] = cur_value;
        }
        m_totalImages++;

        // Write the histogram data to a string
        std::stringstream o;
        for ( int cur_bucket = 0; cur_bucket < m_numBuckets; ++cur_bucket ) {
            o << histogram[ cur_bucket ];
            if ( cur_bucket != ( m_numBuckets - 1 )) {
                o << ',';
            }
        }

        // write the histogram string to a DataFile
        int str_len = o.str().length();
        char * string_bytes = new char[ str_len + 1 ];
        std::string histo_str = o.str();
        histo_str.copy( string_bytes, str_len );
        string_bytes[ str_len ] = 0;
        DataFile * outFile = new DataFile( string_bytes, str_len + 1, "text/csv", DataFile::NEW );
        
        // add the DataFile to the output Dataset
        outputDataset->addDataFile
            ( DataKey
              ( DataKeyElement( inName.getIdentifier(), m_nameDimension )), 
              outFile );

        delete image;
    }

    void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) {
        /** Retrieve the data from the "images" dataset.  Iterate over
         * each FileKey in the data set, retrieving the data file and
         * computing the histogram for each.  Write the histogram to a
         * DataFile and add it to the "histograms" output data set.
         *
         * After iterating over all the images, write the average
         * pixel values to the avg_pixels dataset.
         */

        Dataset * inputDataset = data.getInputDataset( "images" );
        Dataset * outputDataset = data.getOutputDataset( "histograms" );
        DataKeySet inputDataKeys = inputDataset->getAllFileKeys();

        // get the number of buckets parameter from the configuration
        m_numBuckets = data.getConfig().getConfigParamAsInt( "num_buckets" );

        // iterate over each image and compute the histogram
        for ( DataKeySet::iterator i = inputDataKeys.begin();
              i != inputDataKeys.end(); ++i ) {
            DataKey curKey = *i;
            DataFile * f = inputDataset->getDataFile( curKey );
            DataKeyElement inNameElement = curKey.getElement( m_nameDimension );
            computeHistogram( f->getBytes(), f->getSize(), 
                              inNameElement, outputDataset );
        }

        // take the average values array and write it to a string
        char * outStr = new char[ m_numPixels * 20 ];
        int outStrIndex = 0;
        outStrIndex += sprintf( outStr + outStrIndex,
                                "%d %d\n", m_totalImages, m_numPixels );
        for ( int i = 0; i < m_numPixels; ++i ) {
            outStrIndex += sprintf( outStr + outStrIndex,
                                    "%.2f\n", m_averageValues[ i ] );
        }

        // create an DataFile for the avg_histogram and pass it the
        // avg histogram string
        DataFile * outFile = new DataFile( outStr, outStrIndex, "text/plain", DataFile::NEW );

        // write the avg_histogram file to the avg_histogram output Dataset
        Dataset * avgOutDataset = data.getOutputDataset( "intermediate_avg_pixels" );
        DataKeySet::iterator i = inputDataKeys.begin();
        DataKeyElement inNameElement = (*i).getElement( m_nameDimension );
        DataKey avgOutputKey( DataKeyElement( inNameElement.getIdentifier(), 
                                              m_nameDimension ));
        avgOutDataset->addDataFile( avgOutputKey, outFile );
        delete m_averageValues;
        m_averageValues = 0;
    }
			
};

// register this algorithm with mr4c
MR4C_REGISTER_ALGORITHM(histogram, Histograms::create());

class AvgPixelReduce : public Algorithm {
    /** inputs:  directory : intermediate_avg_pixels
     *                       Contains intermediate average pixel files,
     *                       the first line of which is the weight of
     *                       the values (that is, the number of files
     *                       that were used in computing that
     *                       histogram) and subsequent lines are the
     *                       average values of the pixels
     *  outputs: binary    : avg_pixels
     *                       Filename of the avg_pixel file
     *                      
     */


    typedef std::set<DataKey> DataKeySet;

    DataKeyDimension m_nameDimension;
    int              m_numBuckets;
    double *         m_averageValues;
    int              m_totalImages;
    int              m_numPixels;


public:
    AvgPixelReduce() :  m_averageValues( 0 ), m_totalImages( 0 ) {
    }
    
    static Algorithm* create() {
        /**
         * Instantiate an instance of the algortihm and add the input
         * and output datasets
         */
        AvgPixelReduce* algo = new AvgPixelReduce();
        algo->addInputDataset("intermediate_avg_pixels");
        algo->addOutputDataset("avg_pixels");
        return algo;
    }

    
    void computeAvgHistogram( char * data, size_t len ) {
        /**
         * Given an input intermediate histogram, compute the overall average.
         */
        

        // If this is the first time through, create the average
        // values array
        std::string in_str( data, len );
        std::istringstream in( in_str );
        int num_pics = 0;
        int num_pixels = 0;
        in >> num_pics;
        in >> num_pixels;
        if ( m_averageValues == 0 ) {
            // read the number of pixels
            m_averageValues = new double[ num_pixels ];
            memset( m_averageValues, 0, sizeof( double ) * num_pixels );
            m_totalImages = 0;
            m_numPixels = num_pixels;
        }

        for ( int i = 0; i < num_pixels; ++i ) {
            double cur_value = 0;
            in >> cur_value;
            double new_value = ( m_averageValues[ i ] * m_totalImages ) + 
                ( cur_value * num_pics );
            new_value /= ( num_pics + m_totalImages );
            m_averageValues[ i ] = new_value;
        }
        m_totalImages += num_pics;
    }

    void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) {
        /** Retrieve each average pixel file and combine them to create
         * an overall average.
         *
         * After iterating over all the average pixel files, write the average
         * overall pixel values to the avg_pixels dataset.
         */

        Dataset * inputDataset = data.getInputDataset( "intermediate_avg_pixels" );
        Dataset * avgOutputDataset = data.getOutputDataset( "avg_pixels" );
        DataKeySet inputDataKeys = inputDataset->getAllFileKeys();

        // iterate over each intermediate avg pixel file
        for ( DataKeySet::iterator i = inputDataKeys.begin();
              i != inputDataKeys.end(); ++i ) {
            DataKey curKey = *i;
            DataFile * f = inputDataset->getDataFile( curKey );
            computeAvgHistogram( f->getBytes(), f->getSize() );
        }

        // take the average values array and write it to a string
        char * outStr = new char[ m_numPixels * 20 ];
        int outStrIndex = 0;
        for ( int i = 0; i < m_numPixels; ++i ) {
            outStrIndex += sprintf( outStr + outStrIndex,
                                    "%.2f\n", m_averageValues[ i ] );
        }

        // create an DataFile for the avg_histogram and pass it the
        // avg pixel string
        DataFile * outFile = new DataFile( outStr, outStrIndex, "text/plain", DataFile::NEW );

        // write the avg_pixels file to the avg_pixels output Dataset
        avgOutputDataset->addDataFile( DataKey(), outFile );
        delete m_averageValues;
        m_averageValues = 0;
    }
			
};

// register this algorithm with mr4c
MR4C_REGISTER_ALGORITHM( avg_pixel_reduce, AvgPixelReduce::create());

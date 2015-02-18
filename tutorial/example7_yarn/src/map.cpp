//std
#include <iostream>
#include <string>
#include <cstring>
#include <stdlib.h> //exit()
//mr4c
#include "algo_dev_api.h"
#include "mr4c_geo_api.h"

using namespace MR4C;

//extend the Algorithm class                          
class Map : public Algorithm 
{
public:

	//virtual method that will be executed                                                                                                           
	void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) 
	{		
		//****define algorithm here****
		//open native message block	
		std::string nativeHdr="\n*************************NATIVE_OUTPUT*************************\n"; 
		std::cout<<nativeHdr<<std::endl;

		//open image files
		// input/output directory names specified in configuration file
		Dataset* input = data.getInputDataset("imagesIn");
		Dataset* outputHist = data.getOutputDataset("hist");
		
		//get keyspace elements (files in directory by dimension)
		Keyspace keyspace = data.getKeyspace();
		std::vector<DataKeyElement> names = keyspace.getKeyspaceDimension(DataKeyDimension("NAME")).getElements();
				
		
		//... iterate through keys and do work ...
		for ( std::vector<DataKeyElement>::iterator n=names.begin(); n != names.end(); n++ )
		{
			//print dimension name to stdout
			std::cout<<*n<<std::endl;
			
			//get multispectral data
			DataKey skyKey = *n;
			DataFile* skyFile = input->getDataFile(skyKey);
			
			//get gdal dataset
			std::string inputFileName = skyKey.toName("__")+"_input";
			GDALMemoryFile inputMemFile(inputFileName, *skyFile);
			GDALDataset * skyDataset = inputMemFile.getGDALDataset();
			
			//get gdal bands
			GDALRasterBand * blueBand = skyDataset->GetRasterBand(1);
			GDALRasterBand * greenBand = skyDataset->GetRasterBand(2);
			GDALRasterBand * redBand = skyDataset->GetRasterBand(3);
			GDALRasterBand * nirBand = skyDataset->GetRasterBand(4);
			
			//create memory buffers to hold bands
			unsigned int nXSize = redBand->GetXSize();
			unsigned int nYSize = redBand->GetYSize();
			uint16_t * bufferBlue = ( uint16_t *) CPLMalloc(sizeof( uint16_t )*nXSize*nYSize);
			uint16_t * bufferGreen = ( uint16_t *) CPLMalloc(sizeof( uint16_t )*nXSize*nYSize);
			uint16_t * bufferRed = ( uint16_t *) CPLMalloc(sizeof( uint16_t )*nXSize*nYSize);
			uint16_t * bufferNIR = ( uint16_t *) CPLMalloc(sizeof( uint16_t )*nXSize*nYSize);
			
			//gdal read bands into buffer
			GDALDataType gdalType = blueBand->GetRasterDataType() ;
			blueBand->RasterIO( GF_Read, 0, 0, nXSize, nYSize, 
			         bufferBlue, nXSize , nYSize, gdalType , 0, 0 );
			greenBand->RasterIO( GF_Read, 0, 0, nXSize, nYSize, 
			         bufferGreen, nXSize , nYSize, gdalType , 0, 0 );
			redBand->RasterIO( GF_Read, 0, 0, nXSize, nYSize, 
			         bufferRed, nXSize , nYSize, gdalType , 0, 0 );
			nirBand->RasterIO( GF_Read, 0, 0, nXSize, nYSize, 
			         bufferNIR, nXSize , nYSize, gdalType , 0, 0 );

			//make histogram
			uint64_t hist[4096][4] = {{0}};
	      
			//populate histogram
			for (uint64_t i = 0 ; i < nXSize*nYSize ; ++i)
			{
				//blue
				if (bufferBlue[i]<4096) hist[bufferBlue[i]][0]++;
				//green
				if (bufferGreen[i]<4096) hist[bufferGreen[i]][1]++;
				//red
				if (bufferRed[i]<4096) hist[bufferRed[i]][2]++;
				//nir
				if (bufferNIR[i]<4096) hist[bufferNIR[i]][3]++;
			}        
			
			//output histogram
		   	long long unsigned int histSize = 4096 * 4 * sizeof(uint64_t);
			char* histBytes = new char[histSize];
			std::memcpy ( histBytes, &hist[0], histSize);
			DataKey histKey = DataKey(*n);
			DataFile* histData = new DataFile(histBytes, histSize, "text/plain");
			outputHist->addDataFile(histKey, histData);

			inputMemFile.close();
		   
			//close message block		
			std::cout<<nativeHdr<<std::endl;
		}
	}

	//method that's called when algorithm is registered                                                                                                                                                        
	//list input and output datasets here                                                                                                                                                                      
	static Algorithm* create() 
	{
		Map* algo = new Map();
		algo->addInputDataset("imagesIn");
		algo->addOutputDataset("hist");
		return algo;
	}

};

//this will create a global variable that registers the algorithm when its library is loaded.                                                                                                               
MR4C_REGISTER_ALGORITHM(map,Map::create());


//std
#include <iostream>
#include <stdlib.h> //exit()
//mr4c
#include "algo_dev_api.h"
//gdal
#include "gdal_priv.h"
#include "cpl_conv.h" // for CPLMalloc()
#include "cpl_string.h"

//vsi
#include "cpl_vsi.h"
#include "cpl_port.h"
#include <unistd.h>
#include <sys/stat.h>


using namespace MR4C;

//extend the Algorithm class                          
class ImageInfo : public Algorithm {
public:

	//virtual method that will be executed                                                                                                           
	void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) {

		//****define algorithm here****

		//open image file
		// input name specified in configuration file
		Dataset* input = data.getInputDataset("imageIn");
		std::set<DataKey> keys = input->getAllFileKeys();
	
		//... iterate through keys and do work ...
		for ( std::set<DataKey>::iterator i = keys.begin(); i != keys.end(); i++ ) {	
			DataKey myKey = *i;
			DataFile* myFile = input->getDataFile(myKey);
			int fileSize=myFile->getSize();
			GByte * fileBytes = (GByte *) myFile->getBytes();

			//open native message block	
			std::string nativeHdr="\n*************************NATIVE_OUTPUT*************************\n"; 
			std::cout<<nativeHdr<<std::endl;
			
			//create gdal image object and register gdal drivers
			GDALDataset  *poDataset;
			GDALAllRegister();
			
			//create virtual pszFilename from DataFile
			const char *pszFilename = "/vsimem/sample.tif";
			int bTakeOwnership=TRUE;
			VSIFileFromMemBuffer ( pszFilename , fileBytes , fileSize , bTakeOwnership);
			
			//report image info to stdout
			std::cout<<"file name: "<<pszFilename<<"\n"<<std::endl;
			std::cout<<"file size (bytes): "<<fileSize<<"\n"<<std::endl;
			
			//open image using GDAL
			poDataset = (GDALDataset *) GDALOpen( pszFilename, GA_Update );
    		
    		if( poDataset == NULL ){
				std::cout<<"ERROR:Image file "<<pszFilename<<" could not be opened"<<std::endl;
				exit(1);
			}
			
			//report projection
			if( poDataset->GetProjectionRef()  != NULL ){
				std::cout<< "Projection: "<<poDataset->GetProjectionRef()<<"\n"<<std::endl;
			}else{
				std::cout<<"ERROR:Could not determine projection"<<std::endl;
			}
			
			//report dimensions
			std::cout<<"Dimensions (x,y,bands): "<<poDataset->GetRasterXSize()<<" , "<<poDataset->GetRasterYSize()
			<<" , "<<poDataset->GetRasterCount()<<"\n"<<std::endl;
			
			//print origin and pixel size
			double adfGeoTransform[6];
			if( poDataset->GetGeoTransform( adfGeoTransform ) == CE_None ){
				std::cout<<"Origin: "<<adfGeoTransform[0]<<" , "<<adfGeoTransform[3]<<std::endl;
				std::cout<<"Pixel Size: "<<adfGeoTransform[1]<<" , "<<adfGeoTransform[5]<<std::endl;
			}
			
			//gdal get first band
			GDALRasterBand *poBand;
        	poBand = poDataset->GetRasterBand( 1 );
			
			//create memory buffer to hold band
		    int nXSize = poBand->GetXSize();
		    int nYSize = poBand->GetYSize();
		    unsigned char * buffer = (unsigned char *) CPLMalloc(sizeof(unsigned char)*nXSize*nYSize);
		    
		    //gdal read band into buffer
		    poBand->RasterIO( GF_Read, 0, 0, nXSize, nYSize, 
		                      buffer, nXSize , nYSize, GDT_Byte, 0, 0 );
		    
		    //run algorithm on values in buffer
		    for ( int i=0 ; i < nXSize*nYSize ; i++ ) {
		    	if ( (int) buffer[i] > 0 ) {
		    		//add algorithm here
		    	}
		    }
			
			//close message block		
			std::cout<<nativeHdr<<std::endl;

			//free buffers
		    CPLFree(buffer);
		    GDALClose(poDataset);
		}
	}

	//method that's called when algorithm is registered                                                                                                                                                        
	//list input and output datasets here                                                                                                                                                                      
	static Algorithm* create() {
		ImageInfo* algo = new ImageInfo();
		algo->addInputDataset("imageIn");
		return algo;
		}
	};

//this will create a global variable that registers the algorithm when its library is loaded.                                                                                                               
MR4C_REGISTER_ALGORITHM(imageinfo,ImageInfo::create());

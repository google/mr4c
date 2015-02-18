//std
#include <iostream>
#include <stdlib.h> //exit()
//mr4c
#include "algo_dev_api.h"
#include "mr4c_geo_api.h"

using namespace MR4C;

//extend the Algorithm class                          
class Content : public Algorithm 
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
		Dataset* output = data.getOutputDataset("imagesOut");
		
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
			
			//unique name for an in-memory GDAL file
			std::string inputFileName = skyKey.toName("__")+"_input";
		
			//get gdal dataset
			GDALMemoryFile inputMemFile(inputFileName, *skyFile);
			GDALDataset * skyDataset = inputMemFile.getGDALDataset();
			
			//get gdal bands
			GDALRasterBand * blueBand = skyDataset->GetRasterBand(1);
			GDALRasterBand * greenBand = skyDataset->GetRasterBand(2);
			GDALRasterBand * redBand = skyDataset->GetRasterBand(3);
			GDALRasterBand * nirBand = skyDataset->GetRasterBand(4);
				
			//create memory buffers to hold bands
			int nXSize = redBand->GetXSize();
			int nYSize = redBand->GetYSize();
			uint16_t * bufferBlue = (uint16_t *) CPLMalloc(sizeof(uint16_t)*nXSize*nYSize);
			uint16_t * bufferGreen = (uint16_t *) CPLMalloc(sizeof(uint16_t)*nXSize*nYSize);
			uint16_t * bufferRed = (uint16_t *) CPLMalloc(sizeof(uint16_t)*nXSize*nYSize);
			uint16_t * bufferNIR = (uint16_t *) CPLMalloc(sizeof(uint16_t)*nXSize*nYSize);
			//output
			uint16_t * bufferClass = (uint16_t *) CPLMalloc(sizeof(uint16_t)*nXSize*nYSize);
			
			//gdal read bands into buffer
			blueBand->RasterIO( GF_Read, 0, 0, nXSize, nYSize, 
			              bufferBlue, nXSize , nYSize, GDT_UInt16, 0, 0 );
			greenBand->RasterIO( GF_Read, 0, 0, nXSize, nYSize, 
			              bufferGreen, nXSize , nYSize, GDT_UInt16, 0, 0 );
			redBand->RasterIO( GF_Read, 0, 0, nXSize, nYSize, 
			              bufferRed, nXSize , nYSize, GDT_UInt16, 0, 0 );
			nirBand->RasterIO( GF_Read, 0, 0, nXSize, nYSize, 
			              bufferNIR, nXSize , nYSize, GDT_UInt16, 0, 0 );
	
			
			//classify pixels
			for (int i=0 ; i < nXSize*nYSize ; i++ )
			{				
				//unclassified
				uint16_t pixelClass = 0;
				if (bufferBlue[i]>0 && bufferGreen[i]>0 && bufferRed[i]>0 && bufferNIR[i]>0 )
				{
					//ground
					pixelClass = 1;
					
					//classify pixels
					double ndvi = ((double)bufferNIR[i]-(double)bufferRed[i])/((double)bufferNIR[i]+(double)bufferRed[i]);
					double water = ((double)bufferBlue[i]-(double)bufferRed[i])/(double)(bufferRed[i]+(double)bufferBlue[i]);
					
					if ( ndvi>0.1 )
					{
						//vegetation
						pixelClass = 128;
					}
					else if (water > 0.1 )
					{
						//water
						pixelClass = 256;
					}
				}
				//write to buffer
				bufferClass[i]=pixelClass;
						
			}
			
			// create in memory storage for GDAL output file
			std::string outputFileName = skyKey.toName("__")+"_output";
			GDALMemoryFile outMemFile(outputFileName);
		
			// create the output dataset
			GDALDataset* outDataset = newGDALDataset(
				outMemFile.getPath(),
				"Gtiff",
				nXSize,
				nYSize,
				1, // 1 band
				GDT_UInt16
			);
			outMemFile.setGDALDataset(outDataset);
			
			// Write results into a band
			GDALRasterBand * gBand = outDataset->GetRasterBand(1);
			gBand->RasterIO( GF_Write, 0, 0, nXSize, nYSize,
				bufferClass, nXSize, nYSize, GDT_UInt16,0,0 );
		
			// copy metadata
			double adfGeoTransform[6];
			const char * projection = skyDataset->GetProjectionRef();
			outDataset->SetProjection(projection);
			skyDataset->GetGeoTransform( adfGeoTransform );
			outDataset->SetGeoTransform( adfGeoTransform );
		
			// close the files
			inputMemFile.close();
			outMemFile.close();
			
			// output file in the output folder
			DataKey outKey = DataKey(*n);
			DataFile* fileData = outMemFile.toDataFile("image/tif");
			output->addDataFile(outKey, fileData);
		
			//close message block		
			std::cout<<nativeHdr<<std::endl;
				
			//free buffers
			CPLFree(bufferBlue);
			CPLFree(bufferGreen);
			CPLFree(bufferRed);
			CPLFree(bufferNIR);
			CPLFree(bufferClass);
		}
	}

	//method that's called when algorithm is registered                                                                                                                                                        
	//list input and output datasets here                                                                                                                                                                      
	static Algorithm* create() 
	{
		Content* algo = new Content();
		algo->addInputDataset("imagesIn");
		algo->addOutputDataset("imagesOut");
		return algo;
	}

};

//this will create a global variable that registers the algorithm when its library is loaded.                                                                                                               
MR4C_REGISTER_ALGORITHM(content,Content::create());


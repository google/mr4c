#include "algo_dev_api.h"
#include <iostream>

using namespace MR4C;

//extend the Algorithm class                          
class ChangeImage : public Algorithm {
public:

	//virtual method that will be executed                                                                                                           
	void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) {

		//****define algorithm here****

		//open image file
		// input name specified in configuration file
		Dataset* input = data.getInputDataset("imageIn");
		std::set<DataKey> keys = input->getAllFileKeys();
	
		//... iterate through input keys and do work ...
		for ( std::set<DataKey>::iterator i = keys.begin(); i != keys.end(); i++ ) {	
			DataKey myKey = *i;
			DataFile* myFile = input->getDataFile(myKey);
			int fileSize=myFile->getSize();
			char * fileBytes=myFile->getBytes();

			//open native message block	
			std::string nativeHdr="\n*************************NATIVE_OUTPUT*************************\n"; 
			std::cout<<nativeHdr<<std::endl;		
			
			//report image info to stdout
			std::cout<<"  Image Loaded"<<std::endl;
			std::cout<<"  "<<fileSize<<" bytes"<<std::endl;
			
			//print original file contents
			std::cout<<"  original file contents: "<<fileBytes;
			
			//change pixel values
			for (int b=0;b<fileSize-1;b++){
				fileBytes[b]++;
			}
			
			//print new output file contents
			std::cout<<"  output file contents: "<<fileBytes;

			//close message block		
			std::cout<<nativeHdr<<std::endl;
			
			// output file in the output folder
			Dataset* output = data.getOutputDataset("imageOut");
			DataFile* fileData = new DataFile(fileBytes, fileSize, "testOut.bin");
			output->addDataFile(myKey, fileData);
		}
	}

	//method that's called when algorithm is registered                                                                                                                                                        
	//list input and output datasets here                                                                                                                                                                      
	static Algorithm* create() {
		ChangeImage* algo = new ChangeImage();
		algo->addInputDataset("imageIn");
		algo->addOutputDataset("imageOut");
		return algo;
		}
	};

//this will create a global variable that registers the algorithm when its library is loaded.                                                                                                               
MR4C_REGISTER_ALGORITHM(changeimage,ChangeImage::create());

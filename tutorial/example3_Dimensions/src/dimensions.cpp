#include "algo_dev_api.h"
#include <iostream>
#include <vector>

using namespace MR4C;

//extend the Algorithm class                          
class Dimensions : public Algorithm {
public:

	//virtual method that will be executed                                                                                                           
	void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) {

		//****define algorithm here****

		//open image dataset
		// input name specified in configuration file
		Dataset* input = data.getInputDataset("imagesIn");
		
		//get keyspace elements (files in directory by dimension)
		Keyspace keyspace = data.getKeyspace();
		std::vector<DataKeyElement> names = keyspace.getKeyspaceDimension(DataKeyDimension("NAME")).getElements();

		//open native message block	
		std::string nativeHdr="\n*************************NATIVE_OUTPUT*************************\n"; 	
		std::cout<<nativeHdr<<std::endl;
		
		//do something with dimensions
		std::cout<<"dimensions:"<<std::endl;
		for ( size_t i=0; i < names.size() ; i++ ) {	
		
			//report element
			DataKey myKey = names[i];
			std::cout<<myKey<<std::endl;
			
			//open file for each key
			DataFile* myFile = input->getDataFile(myKey);
			int fileSize=myFile->getSize();
			char * fileBytes=myFile->getBytes();
			
			//report image info to stdout
			std::cout<<" Image Loaded"<<std::endl;
			std::cout<<" "<<fileSize<<" bytes"<<std::endl;
			
			//print original file contents
			std::cout<<" File contents: "<<fileBytes;
		}
			
		//close message block		
		std::cout<<nativeHdr<<std::endl;
	}

	//method that's called when algorithm is registered                                                                                                                                                        
	//list input and output datasets here                                                                                                                                                                      
	static Algorithm* create() {
		Dimensions* algo = new Dimensions();
		algo->addInputDataset("imagesIn");
		return algo;
		}
	};

//this will create a global variable that registers the algorithm when its library is loaded.                                                                                                               
MR4C_REGISTER_ALGORITHM(dimensions,Dimensions::create());

#include "algo_dev_api.h"
#include <iostream>
#include <string.h>
#include <jansson.h>

using namespace MR4C;

//extend the Algorithm class                          
class JSON : public Algorithm {
public:

	//virtual method that will be executed                                                                                                           
	void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) {

		//****define algorithm here****

		//open image file
		// input name specified in configuration file
		Dataset* input = data.getInputDataset("metadataIn");
		std::set<DataKey> keys = input->getAllFileKeys();
	
		//... iterate through keys and do work ...
		for ( std::set<DataKey>::iterator i = keys.begin(); i != keys.end(); i++ ) {	
			DataKey myKey = *i;
			DataFile* myFile = input->getDataFile(myKey);
			int fileSize=myFile->getSize();
			const char * fileBytes=myFile->getBytes();

			//open native message block	
			std::string nativeHdr="\n*************************NATIVE_OUTPUT*************************\n"; 
			std::cout<<nativeHdr<<std::endl;		

			//report metadata name
			std::cout<<myKey<<std::endl;

			//initialize json variables
			json_t *root;
			json_error_t error;
			root = json_loads(fileBytes, 0, &error);
			if(!root){
				std::cout<<error.line<<error.text<<std::endl;
			}
			
			//read json objects
			const char *message_text;
			
			json_t *metadata,*copyright, *rpcHorizontalCoordinateSystem;
			
			//metadata block
			metadata = json_object_get(root, "metadata");
			if(!json_is_object(metadata)){
					std::cout<<"metadata is not an object"<<std::endl;
			}

			copyright = json_object_get(metadata, "copyright");
			if(!json_is_string(copyright)){
					std::cout<<"copyright is not an string"<<std::endl;
			}
			message_text = json_string_value(copyright);
			std::cout<<message_text<<std::endl;
			
			rpcHorizontalCoordinateSystem = json_object_get(metadata, "rpcHorizontalCoordinateSystem");
			if(!json_is_string(rpcHorizontalCoordinateSystem)){
					std::cout<<"rpcHorizontalCoordinateSystem is not an string"<<std::endl;
			}
			message_text = json_string_value(rpcHorizontalCoordinateSystem);
			std::cout<<message_text<<std::endl;

			//sensor block
			json_t *sensor,*satelliteName;
			
			sensor = json_object_get(root, "sensor");
			if(!json_is_object(sensor)){
					std::cout<<"sensor is not an object"<<std::endl;
			}
			
			satelliteName = json_object_get(sensor, "satelliteName");
			if(!json_is_string(satelliteName)){
					std::cout<<"satelliteName is not an string"<<std::endl;
			}
			message_text = json_string_value(satelliteName);
			std::cout<<message_text<<std::endl;
			
			//free resources
			json_decref(root);
			
			//close message block		
			std::cout<<nativeHdr<<std::endl;
		}
	}

	//method that's called when algorithm is registered                                                                                                                                                        
	//list input and output datasets here                                                                                                                                                                      
	static Algorithm* create() {
		JSON* algo = new JSON();
		algo->addInputDataset("metadataIn");
		return algo;
		}
	};

//this will create a global variable that registers the algorithm when its library is loaded.                                                                                                               
MR4C_REGISTER_ALGORITHM(json,JSON::create());

//std
#include <iostream>
#include <string>
#include <cstring>
#include <stdlib.h> //exit()
//mr4c
#include "algo_dev_api.h"

using namespace MR4C;

//extend the Algorithm class                          
class RandomAccess : public Algorithm
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
			std::cout<< "reading from: " <<*n<<std::endl;
			
			//get a file from the dataset
			DataKey key = *n;
			RandomAccessFile* randIn = input->getDataFileForRandomAccess(key);
			   
			//get the file size in bytes
			size_t fileSize = randIn->getFileSize();
			std::cout<<"file size in bytes: "<<	fileSize <<std::endl;
			
			//instantiate buffer	
			size_t bufferSize = 100;
			char * data = new char [bufferSize];
			
			//set position in file
			randIn->setLocation( bufferSize+1 );
			
			//read bytes from this position
			size_t read = randIn->read ( data,bufferSize );
			std::cout<< "read "<<read<< " bytes from file at byte "<< randIn->getLocation() <<":\n"
			    	<< *data <<std::endl;
			
			//skip backward in file
			randIn->skipBackward( bufferSize+1 );
			
			//read bytes from new position
			read = randIn->read( data,bufferSize );
			std::cout<< "read "<<read<< " bytes from file at byte "<< randIn->getLocation() <<":\n"
			    	<< *data <<std::endl;
			
			//make new output file
			DataFile * fileOut = new DataFile("text/plain");
    			WritableRandomAccessFile * randOut = output->addDataFileForRandomAccess(key, fileOut);
			
			//set output size
			randOut->setFileSize(1000);
			
			//set position
			randOut->setLocationFromEnd(500);
			
			//write data
			randOut->write(data, bufferSize);
			
			//modify buffer data
			char * temp = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
			std::memcpy ( data, &temp[0], bufferSize);
			
			//skip forward
			randOut->skipForward(bufferSize);
			
			//write new buffer
			randOut->write(data, bufferSize);
			
			//read bytes that we just wrote
			read = randOut->read( data,bufferSize );
			std::cout<< "read "<<read<< " bytes from file at byte "<< randOut->getLocation() <<":\n"
			    	<< *data <<std::endl;
			
			//delete buffer
			delete[] data;
			
			//close files
			randIn->close();
			randOut->close();
			
			//close message block		
			std::cout<<nativeHdr<<std::endl;
		}
	}

	//method that's called when algorithm is registered                                                                                                                                                        
	//list input and output datasets here                                                                                                                                                                      
	static Algorithm* create() 
	{
		RandomAccess* algo = new RandomAccess();
		algo->addInputDataset("imagesIn");
		algo->addOutputDataset("imagesOut");
		return algo;
	}

};

//this will create a global variable that registers the algorithm when its library is loaded.                                                                                                               
MR4C_REGISTER_ALGORITHM( RandomAccess , RandomAccess::create() );

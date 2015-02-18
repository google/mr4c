//std
#include <iostream>
#include <string>
#include <cstring>
#include <stdlib.h> //exit()
//mr4c
#include "algo_dev_api.h"

using namespace MR4C;

//extend the Algorithm class
class Reduce : public Algorithm {
public:

	//virtual method that will be executed                                                                                                           
	void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) 
	{		
		
		//****define algorithm here****
		//open native message block	
		std::string nativeHdr="\n*************************NATIVE_OUTPUT*************************\n"; 
		std::cout<<nativeHdr<<std::endl;

		//open files
		// input/output directory names specified in configuration file
		Dataset* input = data.getInputDataset("hist");
		Dataset* output = data.getOutputDataset("summaryOut");
		
		//get keyspace elements (files in directory by dimension)
		Keyspace keyspace = data.getKeyspace();
		std::vector<DataKeyElement> names = keyspace.getKeyspaceDimension(DataKeyDimension("NAME")).getElements();
		
		//make summary histogram
		uint64_t histSummary[4096][4] = {{0}};
		long long unsigned int tempSize = 4096 * 4 * sizeof(uint64_t);
		
		//... iterate through keys and do work ...
		for ( std::vector<DataKeyElement>::iterator n=names.begin(); n != names.end(); n++ )
		{
			//print dimension name to stdout
			std::cout<<*n<<std::endl;
			
			//get input data
			DataKey key = *n;
			DataFile* file = input->getDataFile(key);
	      		char * bytes = file->getBytes();
			
			//make summary histogram
			uint64_t histTemp[4096][4] = {{0}};
		   
			//read input histogram
		   	std::memcpy( histTemp, bytes , tempSize );
			
			//add to summary histogram
			for (int b = 0 ; b < 4 ; ++b)
			{
				for (int h = 0 ; h < 4096 ; ++h)
				{
					histSummary[h][b] += histTemp[h][b];
				}
			}
		}
		
		//write out summary string
		std::string outString = "band,";
		for (int h=0 ; h < 4096 ; ++h) outString += std::to_string(h) + ",";
		outString +="\n";
		for (int b = 0 ; b < 4 ; ++b){
			   outString = outString + std::to_string(b+1) + ",";
			   for (int h=0 ; h < 4096 ; ++h){
			      outString = outString + std::to_string(histSummary[h][b]) + ",";
			   }
			outString = outString + "\n";
		}          
      
		//write summary to file
		char* histBytes = new char[outString.length()+1];
		std::memcpy ( histBytes,  outString.c_str(), outString.length()); 
		long long unsigned int histSize = outString.size() ;
		DataKey histKey;
		DataFile* histData = new DataFile(histBytes, histSize, "binary");
		output->addDataFile(histKey, histData);
	   
		//close message block		
		std::cout<<nativeHdr<<std::endl;
	}

	//method that's called when algorithm is registered                                                                                                                                                        
	//list input and output datasets here                                                                                                                                                                      
	static Algorithm* create() 
	{
		Reduce* algo = new Reduce();
		algo->addInputDataset("hist");
		algo->addOutputDataset("summaryOut");
		return algo;
	}

};

//this will create a global variable that registers the algorithm when its library is loaded.                                                                                                               
MR4C_REGISTER_ALGORITHM(reduce,Reduce::create());


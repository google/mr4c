#include "algo_dev_api.h"
#include <iostream>

using namespace MR4C;

//extend the Algorithm class                          
class HelloWorld : public Algorithm {
public:

	//virtual method that must be implemented                                                                                                                                                                  
	void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) {
	
		//add your code here

		//open native message block	
		std::string nativeHdr="\n*************************NATIVE_OUTPUT*************************\n"; 
		std::cout<<nativeHdr<<std::endl;
		
		//print hello world message to stdout    
		std::cout << "  Hello World!!" << std::endl;
		
		//close message block		
		std::cout<<nativeHdr<<std::endl;                
	}

	//method that's called when algorithm is registered                                                                                                                                                        
	//list input and output datasets here                                                                                                                                                                      
	static Algorithm* create() {
	    HelloWorld* algo = new HelloWorld();
	    return algo;
	}
};

//this will create a global variable that registers the algorithm when its library is loaded.                                                                                                               
MR4C_REGISTER_ALGORITHM(HelloWorld,HelloWorld::create());

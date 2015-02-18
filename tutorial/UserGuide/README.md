MR4C User Guide
=========

##MR4CBasics

Now that you have installed and tested MR4C, lets learn some of the MR4C fundamentals that might be a bit 
unfamiliar.

###Datasets

MR4C uses a "dataset" model to group all the relevant data and make it available to your algorithm.
Traditionally, we think of datasets as files, and we address them using paths.
With MR4C, we can address individual files if necessary, but more often we refer 
the algorithm to a directory containing many files and collectively group them as a dataset.
In addition, part of the dataset could be metadata that give us some important information linked to
the dataset or a particular file.


###Keyspace

The keyspace is an index of unique elements in the dataset. 
Each key refers to a particular peice of the data without having to keep track of a lot paths.
This can be especially handy when we are operating on a large cluster where all of the files 
are not necessarily local.

###Dimensions

Dimensions are a useful abstraction for separating datasets along important axes. For example,
you may have a series of image frames from two different satellites and you might name thecm something like:
"sat1_frame1" , "sat1_frame2", "sat2_frame1", "sat2_frame2". You might define two dimensions for this dataset:
"sat" and "frame" and group elements appropriately : sat(1,2) and frame(1,2). Perhaps you might want to compare frame1 from both 
satellites, or both frames for each satellite independently. MR4C dimensions allow you to define your datasets
using criteria from the original file names even though we aren't reading in the files directly.

###Configuration

In addition to a shared object file containing an algorithm, 
MR4C will also require a configuration file in [json](http://www.json.org/) format. 
This configuration file can keep track of parameters that you might like to change without having to recompile your algorithm.
There are several important object types available in a mr4c configuration file:
* algorithm shared object and class name(s)
* input datasets
* outputs datasets
* mapper to determine the elements of any dimensions
* input parameters
 
###Uniform Resource Identifiers (URIs)

URIs in MR4C can be specified using any of the following schemes:
* HDFS: hdfs://host/file_path or hdfs:///file_path
* File: file://host/file or file:///file_path
* Local File: Provide an absolute or relative local file path (this is a scheme-less URI).
Relative paths are relative to the current working directory, generally the project root if you are running from the project.



## DefineMR4CAlgorithm
### Algorithm Implementation

For the purposes of this documentation, an algorithm is any user-provided code that performs an operation or 
series of operations on a piece of data.

#### Implement

Algorithms are implemented by extending the Algorithm class. To do so, implement the virtual method executeAlgorithm.

#### Configure

After instantiating the algorithm, add the names of expected input and output datasets, and expected dimensions, 
to their algorithm instances.

#### Register

An instance of the algorithm should be registered with the AlgorithmRegistry. 
The easiest way to do this is via the macro MR4C_REGISTER_ALGORITHM. 
This will create a global variable that registers the algorithm when its library is loaded.

#### Deploy

The algorithm and its registration mechanism should be compiled into a shared library. 
Because MR4C supports the several C++11 features your shared object should be compiled with the flag -std=c++0x.

#### Example

The following simple class illustrates algorithm implementation:

	#include "algo_dev_api.h"
	#include <iostream>

	using namespace MR4C;

	//extend the Algorithm class                                                                                                                                                                                  
	class Example : public Algorithm 
	{
	public:
		//virtual method that must be implemented
		void executeAlgorithm(AlgorithmData& data, AlgorithmContext& context) 
		{
			//pointers to datasets mentioned in the configuration file                                   
			Dataset* output = data.getOutputDataset("output1");
			Dataset* input = data.getInputDataset("input1");
			
			//iterate over the keys                                                                                                                                                                                  
			std::set<DataKey> keys = input->getAllFileKeys();
			std::set<DataKey>::iterator iter = keys.begin();
			
			for ( ; iter!=keys.end(); iter++ ) 
			{
		
			      //use the current key to access the DataFile object from the input                                                                                                                                     
			      //you could then access the bytes of the file with 'file->getBytes()'                                                                                                                                  
			      DataKey key = *iter;
			      DataFile* file = input->getDataFile(key);
		
			      //for example purposes, we print the size                                                                                                                                                              
			      int size = (int) file->getSize();
			      std::cout << "File has " << size << " bytes" << std::endl;
		
			      //add data file to output dataset                                                                                                                                                                      
			      output->addDataFile(key,file);
		
			      //Free the memory containing the file bytes.                                                                                                                                                           
			      file->release();
			}
		}
	
		//method that's called when algorithm is registered                                                                                                                                                        
		//list input and output datasets here                                                                                                                                                                      
		static Algorithm* create() {
		Example* algo = new Example();
		algo->addInputDataset("input1");
		algo->addOutputDataset("output1");
		return algo;
		}
	};

	// This will create a global variable that registers the algorithm when its library is loaded.
	MR4C_REGISTER_ALGORITHM(example,Example::create());
	

### Input/Output

Input and output blocks of your shared object must read and write MR4C byte arrays. 
MR4C has a dataset class that can be used in a similar way to fread/fwrite (C) or fstream (C++)
This section illustrates how to handle I/O in MR4C.

In this example, we are reading a series of files that are all in the same directory one at a time. 
The scheme in the json configuration file would be "directory" and each file would be opened as "input".
Adding an iterator through the keys would open each file and we can run an algorithm against them.

##### Input

	// input name specified in configuration file
	Dataset* input = data.getInputDataset("input1");
	std::set<DataKey> keys = input->getAllFileKeys();
	
	//... iterate through keys and do work ...
	for ( ; iter!=keys.end(); iter++ ) {
	
		DataKey MyKey = *iter;
		DataFile* myFile = input->getDataFile(myKey);
		//myFile->getSize(), myFile->getBytes() replace the fseek/ftell
	}

##### Output

	// output name specified in configuration file
	Dataset* output = data.getOutputDataset("output1");
	DataFile* fileData = new DataFile(myfile.bytes, myfile.num_bytes, "image/tiff");
	output->addDataFile(Key, fileData);

### Progress Reporting and Logging

Add MR4C functions for progress reporting and logging.  Progress reporting tells the MR4C platform that your algorithm is computing successfully, allowing for efficient management of platform resources.  You can enable logging with a logger instantiated by MR4C (log4cxx) or add your own logger.

The following examples illustrate MR4C’s reporting and logging functions:

#### Progress Reporting

	context.progress(100*progress/(0.0+total), "%d out of %d images created", progress, total);

#### Logging

	context.log( MR4C::Logger::INFO, "Performing Calculation");

## Define MR4C Configuration File

### Configuration Files

Create a configuration file to execute your algorithm.  Identify your algorithm object name ("name"), library ("artifact"), input/output data, and parameters.  The following example is a simple configuration file:

	{
	        "algoConfig" : {
	          "inline" : {
	            "name" : "example",
	              "type" : "NATIVEC",
	              "artifact" : "myLibrary",
	              "dimensions" : [
	              {
	                "name" : "dim1",
	                "canSplit" : true
	              }
	            ]
	          }
	        },
	        "inputs" : {
	                "input1" : {
	                        "scheme" : "directory",
	                        "location" : "/path/to/input/directory"
	                }
	        },
	        "outputs" : {
	                "output1" : {
	                        "scheme" : "simple",
	                        "location" : "/path/to/output/directory"
	                }
	        },
		"params" : {
			 "image_scale" : 2
		}
	}
	
In addition to algorithm configuration, we often use a dataset configuration to map keys to files when we use a directory scheme.
This file is called dataset.json and it includes a pattern to interpret the file names and mape them to dimensions. 
For example:

	{
    		"mapper" : {
        		"pattern" : "ss01_c${SENSOR}_${FRAME}_${IMAGE_TYPE}_${IMAGE_ID}_1.5bps.jpc",
        		"dimensions" : ["FRAME" , "SENSOR" , "IMAGE_TYPE" ]
    		},
    		"ignore" : true
    		"selfConfig" : true
	}

We define a pattern that parses the file names and returns a variable for each dimension. 
Additionally, we have some optional parameters:
* ignore flag that can be used to ignore files that don't fit the pattern. True will ignore, false will fail.
* selfConfig flag for input files will determine whether a self-configuration file will over-ride these settings if present.

In our algorithm we could read keys from these dimensions defined in our configuration file using something like:
	
	//define keyspace
	const Keyspace* m_keyspace;
	
	//vector to hold elements of dimension
	std::vector<DataKeyElement> m_frames;
	
	//read in dataset (defined as input1 in the config file)
	Dataset* input = data.getInputDataset("input1");
	
	//get Frame dimension
	input->addExpectedDimension(DimensionCatalog::toDimension(DimensionCatalog::FRAME));
	
	//get keys into keyspace
	m_keyspace = &m_data->getKeyspace();
	
	//read frame dimension into frames vector
	m_frames = DimensionCatalog::findDimension(*m_keyspace, DimensionCatalog::FRAME).getElements();

### Reading Parameters

In addition to reading input datasets, it is sometimes necessary to read input parameters.  Parameters can be implemented in your code as illustrated by the following example:

	scale = data.getConfig().getConfigParamAsInt( "image_scale" );

### Runtime Parameters (*Optional*)

If templated, anything written into the configuration file can be overridden at runtime.  For example, in the following excerpt from a configuration file, the location field is template with ${output_directory} rather than a real filesystem path.

	...
	       "outputs" : {
	                "output1" : {
	                        "scheme" : "simple",
	                        "location" : "${output_directory}"
	                }
	        }
	...

To specify runtime parameters in the command line, execute mr4c and pass in variable_name=value, where “variable_name” is the name of the template variable.

	mr4c ./init.json output_directory=/path/to/output/directory

## Test Locally

To test the implementation on your local machine, run the following for an example configuration file named *init.json*.

	mr4c ./init.json

## Run on the cluster

Upload your shared object to any cluster using MR4C and run against large datasets with only minor configuration changes!

## Support

Thank you for getting started with MR4C!  Again, please contact mr4c@googlegroups.com with any questions, comments, or feedback.


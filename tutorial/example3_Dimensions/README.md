# Dimensions

This example builds on the IO example but instead of specifying files for input and output this algorithm reads/writes all files to/from a folder/URI using dimensions. This makes the algrithm very flexible and scale to very large datasets with a lot of elements.

### Configuration:
We use the mappers in dimension.json to configure our dataset dimensions:

	"mapper" : {
		"pattern" : "test_${NAME}.bin",
		"dimensions" : ["NAME"]
	},
      
This construction allows for any files in the input or output folder that fit the pattern to be interpreted as elements in the NAME dimension. In out algorithm we can use these dimensions as follows:

	//get keyspace elements (files in directory by dimension)
	Keyspace keyspace = data.getKeyspace();
	std::vector<DataKeyElement> names = keyspace.getKeyspaceDimension(DataKeyDimension("NAME")).getElements();
	
	//iterate through the elements in the NAMES dimension
	for ( size_t i=0; i < names.size() ; i++ ) {
		
		//get key
		DataKey myKey = names[i];
		
		//open file for each key
		DataFile* myFile = input->getDataFile(myKey);
	}
	

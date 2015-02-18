Using Random Access within a DataFile
===========
In the previous examples we have assumed that the dataset is made up of small files that can be read into memory one at a time. However sometimes we need to read part of a file or write to a specific part of a file. This example illustrates how to use the MR4C RandomAccessFile class to deal with larger and/or more complex I/O.

###Sequential Read/Write
If all you need to do is read/write through a file using a small buffer to avoid loading the whole thing at once you can use a MR4C::DataFile object and use the read, write , and skip functions. These methods will be more performant because they do not require a temporary file and can access data directly through HDFS.

While this example is focused on random access, you can easily do the same thing directly with a DataFile object, only with the limitation that you have to move sequentially forward through the file:

    //get DataFile
    DataFile * file = input->getDataFile(key);
    
    //read sequentially through file
    while ( file->read( buffer , bufferSize ) > 0 )
    {
      //do something with your buffer here
      
      //skip to next buffer
      file->skip(buffersize);
    }

###Random Access
If you need to use a variable size buffer and read and write to any part of a file then you will need to instantiate a RandomAccessFile object and use the similar member functions.

In the example, we read an input dataset and iterate through the dataset keys and instantiate a RandomAccessFile object for each file. We then read some random blocks into a 100 byte buffer and print them to stdout. Finally, we create some output files and write some of the content that we extracted from the input files as well as some modified content to arbitrary locations within a 1000 byte file.

Input and output datasets are stored in HDFS, please refer to the RandomAccess.json and RandomAccess.sh files to understand the staging process.

###Execution Example
To execute:

        ./RandomAccess.sh
After execution:

        hdfs dfs -ls skysatOut
Each file is 1000 bytes because we forced the size with 

        randOut->setFileSize(1000);
This will populate the 1000 bytes with NULL values and write only to the specific locations that we set. We could easily skip this step and let the writes determine the file size automatically, but sometimes you may want to mandate a size.

To print out all the output files:

        hdfs dfs -cat skysatOut/*_out
You will see less than 1000 characters for each one. Most of file was populated with NULL values because we only wrote 200 characters. One of the buffers we wrote was some random char values from the input dataset, and the other was a series of sequential numbers that we added.

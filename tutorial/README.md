Intro to MR4C Examples:
===========

### EXAMPLES

1.	hello world - set up a basic working algorithm
2.	input output - learn how to read and write datasets
3.	dimensions - organize datasets into categories
4.	externalLib - include classes from an extra library (gdal)
5. read json - an example using Skysat metadata
6. image algo - Use all the previous material to read a Skysat Dataset and run a basic scene content algorithm by calculating the average color.
7. yarn - an example illustrating how to use YARN runtime resource allocation on a basic map/reduce algorithm.
8. mbtiles - an example using the MR4C Geospatial library to export a mbtiles file from a Skysat scene.
9. random access - when you need to deal with large datasets, you will need to read in smaller chunks. This example shows you the various ways that MR4C can help you do this.

### Run in Hadoop

* HDFS commands - use the format:

        hdfs dfs -<COMMAND> <URI>

  - COMMAND: ls, mkdir, rm, get, put etc. Similar to Linux
  - URI : see item 2.
  - for example the command below would list the files at /user/tbowdoin/test:

        hdfs dfs -ls /user/tbowdoin/test/

* URIs - in our cluster we can use hdfs URIs which use the format:

        hdfs:///user/<USER>/<PATH>

  - USER: is your hadoop user
  - PATH: is the hdfs path relative to your hdfs user directory

* packaging external libraries 
  - If you are porting an algorithm from one local machine or cluster to another you should include any linked shared objects along with your algorithm so.
  - This will make your algorithm truely portable to any cluster.

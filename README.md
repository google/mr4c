Introduction to the MR4C repo
=========


### About MR4C

MR4C is an implementation framework that allows you to run native code within the Hadoop execution framework. 
Pairing the performance and flexibility of natively developed algorithms with the unfettered scalability and throughput inherent in Hadoop, 
MR4C enables large-scale deployment of advanced data processing applications.

### Map to this repo

This repository includes user guide, tutorials and source code for the MR4C framework created by Google Inc.
We suggest you run through this repo in the following order:

  1. Make sure that you have all dependencies and build (see below).
  2. Test that MR4C install was successful
    - Run `test_mr4c.sh` from the test directory
  3. Study up on MR4C
    - README.md in the UserGuide directory covers the basic concepts behind MR4C
  4. Run through the example algorithms in the tutorial directory
  5. Build your own algorithm using the examples as templates and let us know if you have questions or comments!
  
### Dependencies

* tested with Ubuntu 12.04 and CentOS 6.5
* tested with CDH 5.2.0 (either MRV1 or YARN)
* ant (1.8.2 min)
* java (1.6 min)
* ivy (2.1 min)
* make (3.8.1 min)
* g++ (4.6.3 min)
* log4cxx (0.10.0)
* jansson (2.2.1 min)
* cppunit (1.12.1 min)
* proj4 (4.8.0 min)
* gdal (1.10 min)

### Build

There are four scripts included to build, clean, deploy and/or remove mr4c. Build with:

    ./build_all
    
Clean previous builds with:

    ./clean_all
    
Deploy to /usr/local/mr4c using:

    ./deploy_all
    
Remove all components with:

    ./remove_all


If you get stuck, have questions, or would like to provide *any* feedback, please don’t hesitate to contact us at mr4c@googlegroups.com. 
Let’s do big things together.

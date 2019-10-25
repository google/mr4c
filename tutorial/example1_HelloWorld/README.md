# MR4C "Hello World" Application

Hello World application for MR4C


## Description

This is the classic "HelloWorld" application that should verify that your build is working correctly.

## Installation
Navigate to the HelloWorld folder and make the project using the folowing commands:

    cd ~/MR4C/examples/example1_HelloWorld
    make
    
## Run Hello World    
    
To run our hello world application:

    ./HelloWorld.sh

This should print out "Hello World" in a block labled "**NATIVE_OUTPUT**" to separate it from the mr4c messages.

## Concepts
To fully understand how a basic MR4C application works, review the following files in your installation folder.

### /src/helloworld.cpp
defines the Example class that executes an algorithm (cout<<"hello world"<<endl;) 
when it is registered with MR4C_REGISTER_ALGORITHM(name, algoPtr);

### makefile
instructions to build the HelloWorld shared object from helloworld.cpp

### /helloworld.json
mr4c configuration file that tells mr4c to call a "HelloWorld" object from the "NATIVEC" library called "HelloWorld"

### Execute
This bash script executes mr4c using the configuration file helloworld.json. 
Additionally, it adds our /lib/helloworld.so to the LD_LIBRARY_PATH variable, making it available to mr4c.

## Conclusion
This should have illustrated some of the basic ideas behind implimenting an algorithm in MR4C.
Assuming that this example worked successfully, please try the ChangeImage example next.
If you have any trouble getting this working or have questions or comments, we would love to hear from you.
Please email mr4c@googlegroups.com with your feedback!

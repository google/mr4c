# MR4C json Example

## Description

This example application illustrates how to read json formated data. 
We will use a SkySat metadata file as an example.

Contact mr4c@googlegroups.com with any question or comments.

## Build

Navigate to the example5_json folder and run the following command:

    make

## Running json.sh

To run the json example, execute the following command: 

    ./json.sh

This will input the file from the input folder, execute the algorithm, 
and output some of the important records stored in the json file to stdout
in the section between the **NATIVE_OUTPUT** header/footer.

## Concepts
If you open the SkySat metadata file from the ./input folder,
you will quickly see that there is a lot of information that 
can be a little difficult to interpret with a simple read. 
The great thing about json format is that it allows you to 
depict very complicated dependencies in a way that can be passed
from one program to another. the Jansson library allows us to easily
read and write these dependencies from/into a file.

For more information about the json format see the [json website](http://json.org) and to
learn more about the Jansson library see the [documentation](https://jansson.readthedocs.org/en/2.5/).


# MR4C Change Image Example


## Description

This example application illustrates simple input/output using MR4C. 

Contact mr4c@googlegroups.com with any question or comments.

## Build
Navigate to the example2_IO folder and run the following commands:

    make

## Running bbChangeImage

To run bbChangeImage execute the following command from the cloned folder: 

    ./changeimage.sh

This will input the file from the input folder, execute the algorithm, 
and output the changed values into a new file in the output folder.
The input and output characters are also printed to stdout in the section of output
between the **NATIVE_OUTPUT** header/footer.

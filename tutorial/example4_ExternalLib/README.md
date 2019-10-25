## Introduction
The ExternalLib example is our first example utilizing a third party library in addition to our own algorithm.
Additionally, we are using GDAL to read a geotiff and report some of the important metadata.
This method can be used to convert a MR4C::Dataset into a GDALDataset
allowing the algorithm to have access to all of the GDAL library classes.
You will notice that this requires an extra step of creating a gdal virtual file,
which is a buffer in memory that can be used in the place of any path references required in third party libraries. MR4C keeps track of datasets elements as raw char pointers without traditional file system paths.


## Introduction
This example builds on everything we learned in the previous examples:

  1. Read in several files from a directory
  2. Organize them by dimension
  3. Use gdal to read the geotiff format files into virtual files in memory
  4. Run a basic image content algorithm to classify each pixel in the buffer as ground, vegetation, or water
  5. write out a gdal geotiff as a virtual file
  6. Use MR4C dimensions to write output files from memory


## References

- Please refer to the [GDAL documentation](http://www.gdal.org/cpl__vsi_8h.html) for more info on how to use virtual files.
- Please download and build [GDAL from source](http://trac.osgeo.org/gdal/wiki/BuildHints).

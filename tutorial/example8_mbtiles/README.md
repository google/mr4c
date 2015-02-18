##Algorithm name: mbtiles

###Description:
MR4C now supports output in MBtiles format for upload to mapbox.com or any other compatible application. The following example illustrates how to output MBtiles using the MR4C Geospatial library.

###Parameters:
* name: required, becomes "name" in mbtiles metadata
* description: optional, becomes description in mbtiles metadata
* version: optional, integer, default=1, becomes version in mbtiles metadata
* type: optional, values are "overlay" and "baselayer", default is "overlay"
* format: optional, values are "jpg" and "png", default is "png"
* minZoom: optional, if not provided algo will compute min zoom so tile size is the smallest tile larger than the area covered by the input image
* maxZoom: optional, if not provided algo will compute max zoom so tile size is the largest tile that does not need to be down-sampled
	

###Input Data:

 * Geotiff
 * 8 or 16 bit rendered values
 * 3 bands will be interpreted as RGB
 * Projection should be [Web Mercator](http://spatialreference.org/ref/sr-org/epsg3857-wgs84-web-mercator-auxiliary-sphere/)

To reproject:

	gdalwarp -wm 2048 -r cubic -t_srs EPSG:3857 input.tif inputWeb.tif

	
###Run Script

The run script is configured to get the input dataset from the local folder and put it to hdfs. 
After the output.mbtiles file is created, the script will get the file to the local folder.
Please update this script if you change the input and output filenames in the conf/runtime.properties file.

/**
  * Copyright 2014 Google Inc. All rights reserved.
  * 
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * 
  *     http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
*/

#ifndef __MR4C_GEO_GDAL_UTILS_H__
#define __MR4C_GEO_GDAL_UTILS_H__

#include "gdal_priv.h"
#include "ogr_spatialref.h"

#include "coord/coord_api.h"
#include "dataset/dataset_api.h"

namespace MR4C {

	/**
	  * Loads GDAL driver for the given format
	  * @throws std::invalid_argument if no driver can be loaded
	*/
	GDALDriver* loadGDALDriver(const std::string& format);
	
	/**
	  * Convenience method to create a new GDALDataset.
	  * The path to use can be obtained from a GDALFile instance
	*/
	GDALDataset* newGDALDataset(
		const std::string& path,
		const std::string& format,
		int width,
		int height,
		int bands,
		GDALDataType type
	);

	/**
	  * Convenience method to create a new GDALDataset by copying another.
	  * The path to use can be obtained from a GDALFile instance
	*/
	GDALDataset* copyGDALDataset(
		const std::string& path,
		GDALDataset* srcDataset,
		const std::string& format
	);

	/**
	  * Convenience method to create a new GDALMemoryFile by copying another GDALFile
	*/
	GDALMemoryFile* copyGDALFileToMemory(
		const std::string& name,
		const GDALFile& srcFile,
		const std::string& format
	);

	/**
	  * Convenience method to create a new GDALLocalFile by copying another GDALFile
	*/
	GDALLocalFile* copyGDALFileToLocal(
		const std::string& dir,
		const std::string& name,
		const GDALFile& srcFile,
		const std::string& format
	);

	/**
	  * Locate a GDALDataset from its GeoTransform.  Assumes an Easting-Northing coordinate system
	*/
	ImageBox locateGDALDataset(GDALDataset* dataset);


	/**
	  * Copy data from one ImageBox to another via GDALDatasets.
	*/
	void copyGDALImage(
		GDALDataset* srcDataset,
		const ImageBox& srcImgBox,
		GDALDataset* destDataset,
		const ImageBox& destImgBox
	);

	/**
	  * Generate transform from the projected coordinate system of the dataset to its underlying geographic coordinate system
	*/
	OGRCoordinateTransformation* generateCoordinateTransform(GDALDataset* dataset);

	/**
	  * Generate transform from a projected coordinate system to its underlying geographic coordinate system
	*/
	OGRCoordinateTransformation* generateCoordinateTransform(OGRSpatialReference* sref);

	/**
	  * Convenience method to set the no data value in each band
	*/
	void setNoDataValue(GDALDataset* dataset, double value);
}

#endif



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

#include <stdexcept>
#include "cpl_conv.h" 
#include "ogr_spatialref.h"

#include "gdal/gdal_api.h"
#include "util/util_api.h"

namespace MR4C {

	GDALDriver* loadGDALDriver(const std::string& format) {
		GDALAllRegister();
		GDALDriver* driver = GetGDALDriverManager()->GetDriverByName(format.c_str());
		if ( driver==NULL ) {
			MR4C_THROW(std::invalid_argument, "No GDAL driver found for format [" << format << "]");
		}
		return driver;
	}


	GDALDataset* newGDALDataset(
		const std::string& path,
		const std::string& format,
		int width,
		int height,
		int bands,
		GDALDataType type
	) {
		GDALDriver* driver = loadGDALDriver(format);
		GDALDataset* dataset = driver->Create(path.c_str(), width, height, bands, type, NULL);
		if ( dataset==NULL ) {
			MR4C_THROW(std::logic_error, "Failed to create GDALDataset with path [" << path << "] and format [" << format << "]");
		}
		return dataset;
	}

	GDALDataset* copyGDALDataset(
		const std::string& path,
		GDALDataset* srcDataset,
		const std::string& format
	) {
		GDALDriver* driver = loadGDALDriver(format);
		GDALDataset* dataset = driver->CreateCopy(path.c_str(), srcDataset, false, NULL, NULL, NULL);
		if ( dataset==NULL ) {
			MR4C_THROW(std::logic_error, "Failed to create GDALDataset (by copy) with path [" << path << "] and format [" << format << "]");
		}
		return dataset;
	}

	void copyGDALFileHelper(
		const GDALFile& srcFile,
		GDALFile& destFile,
		const std::string& format
	) {
		GDALDataset* destDataset = copyGDALDataset(destFile.getPath(), srcFile.getGDALDataset(), format);
		destFile.setGDALDataset(destDataset);
	}

	GDALMemoryFile* copyGDALFileToMemory(
		const std::string& name,
		const GDALFile& srcFile,
		const std::string& format

	) {

		GDALMemoryFile* destFile = new GDALMemoryFile(name);
		copyGDALFileHelper(srcFile, *destFile, format);
		return destFile;
	}

	GDALLocalFile* copyGDALFileToLocal(
		const std::string& dir,
		const std::string& name,
		const GDALFile& srcFile,
		const std::string& format
	) {
		GDALLocalFile* destFile = new GDALLocalFile(dir, name);
		copyGDALFileHelper(srcFile, *destFile, format);
		return destFile;
	}

	ImageBox locateGDALDataset(GDALDataset* dataset) {
		double trans[6]; dataset->GetGeoTransform(trans);
		double x1 = trans[0];
		double y1 = trans[3];
		int width = dataset->GetRasterXSize();
		int height = dataset->GetRasterYSize();
		double x2 = x1 + trans[1] * width;
		double y2 = y1 + trans[5] * height;
		EastNorthCoord nw(x1, y1);
		EastNorthCoord se(x2, y2);
		std::shared_ptr<OGRCoordinateTransformation> ogrTrans(generateCoordinateTransform(dataset));
		std::shared_ptr<EastNorthTrans> enTrans(new GDALCoordTrans(ogrTrans));
		BoundingBox bound(nw,se,enTrans);
		return ImageBox(width, height, bound);
	}

	void copyGDALImage(
		GDALDataset* srcDataset,
		const ImageBox& srcImgBox,
		GDALDataset* destDataset,
		const ImageBox& destImgBox
	) {

		for ( int band=1; band<=srcDataset->GetRasterCount(); band++ )  {
			GDALRasterBand* srcRaster = srcDataset->GetRasterBand(band);
			GDALRasterBand* destRaster = destDataset->GetRasterBand(band);
			GDALDataType srcType = srcRaster->GetRasterDataType();
			if ( srcType==GDT_Unknown ) {
				MR4C_THROW(std::logic_error, "Can't copy dataset of unknown data type");
			}
			int srcPixelSize = GDALGetDataTypeSize(srcType);
			GByte* data = new GByte[srcPixelSize * destImgBox.getWidth() * destImgBox.getHeight()];
		
			CPLErr result = srcRaster->RasterIO(
				GF_Read,
				srcImgBox.getX1(),
				srcImgBox.getY1(),
				srcImgBox.getWidth(),
				srcImgBox.getHeight(),
				data,
				destImgBox.getWidth(),
				destImgBox.getHeight(),
				srcType, 0, 0
			);

			if ( result==CE_Failure ) {
				delete[] data;
				MR4C_THROW(std::logic_error, "GDAL image copy failed on raster band read");
			}

			result = destRaster->RasterIO(
				GF_Write,
				destImgBox.getX1(),
				destImgBox.getY1(),
				destImgBox.getWidth(),
				destImgBox.getHeight(),
				data,
				destImgBox.getWidth(),
				destImgBox.getHeight(),
				srcType, 0, 0
			);

			delete[] data;

			if ( result==CE_Failure ) {
				MR4C_THROW(std::logic_error, "GDAL image copy failed on raster band write");
			}
		}

	}

	OGRCoordinateTransformation* generateCoordinateTransform(GDALDataset* dataset) {
		std::string proj = dataset->GetProjectionRef();
		OGRSpatialReference* sref1 = new OGRSpatialReference(proj.c_str());
		return generateCoordinateTransform(sref1);
	}

	OGRCoordinateTransformation* generateCoordinateTransform(OGRSpatialReference* sref) {
		OGRSpatialReference* sref2 = sref->CloneGeogCS();
		if ( sref2==NULL ) {
			MR4C_THROW(std::logic_error, "Call to CloneGeogCS failed");
		}

		OGRCoordinateTransformation* trans = OGRCreateCoordinateTransformation(sref, sref2);
		if ( trans==NULL ) {
			MR4C_THROW(std::logic_error, "Call to OGRCreateCoordinateTransformation failed");
		}
		return trans;
	}

	void setNoDataValue(GDALDataset* dataset, double value) {
		for (int band=1; band<=dataset->GetRasterCount(); band++ )  {
			GDALRasterBand* rasterBand = dataset->GetRasterBand(band);
			if ( rasterBand->SetNoDataValue(value)==CE_Failure ) {
				MR4C_THROW(std::logic_error, "Failed to set no data value");
			}
		}
	}


}


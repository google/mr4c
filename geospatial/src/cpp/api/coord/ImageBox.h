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

#ifndef __MR4C_GEO_IMAGE_BOX_H__
#define __MR4C_GEO_IMAGE_BOX_H__

#include <string>
#include <ostream>

#include "BoundingBox.h"

namespace MR4C {

class ImageBoxImpl;

/**
  * Defines part or all of an image correpsonding to a BoundingBox.
  * Origin of image x,y coordinates is top left.
*/
class ImageBox {

	public:

		ImageBox();

		ImageBox(
			int width,
			int height,
			const BoundingBox& bound,
			int x=0,
			int y=0
		);

		ImageBox(const ImageBox& box);

		/**
		  * width in pixels of the image or image part
		*/
		int getWidth() const;

		/**
		  * heiight in pixels of the image or image part
		*/
		int getHeight() const;

		/**
		  * x-coordinate of upper left corner
		*/
		int getX1() const;

		/**
		  * y-coordinate of upper left corner
		*/
		int getY1() const;

		/**
		  * x-coordinate of lower right corner
		*/
		int getX2() const;

		/**
		  * y-coordinate of lower right corner
		*/
		int getY2() const;

		BoundingBox getBound() const;

		/**
		  * Generate a window on this ImageBox.  The returned ImageBox will be offset to reflect the offset between bounding boxes
		*/
		ImageBox window(const BoundingBox& bound) const;

		std::string str() const;

		~ImageBox();

		ImageBox& operator=(const ImageBox& box);

		bool operator==(const ImageBox& box) const;
		bool operator!=(const ImageBox& box) const;

	private:

		ImageBoxImpl* m_impl;


};

std::ostream& operator<<(std::ostream& os, const ImageBox& box);

}

#endif




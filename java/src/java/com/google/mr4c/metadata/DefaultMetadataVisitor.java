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

package com.google.mr4c.metadata;

/**
  * Default implementation of MetadataVistitor.  Default for all visitXXX
  * methods is to call visitMetadataElement (except visitXXXPreIteration
  * methods, which are no-ops)
*/
public class DefaultMetadataVisitor implements MetadataVisitor {

	public void visitMapPreIteration(MetadataMap map) {}

	public void visitMapPostIteration(MetadataMap map) {
		visitMetadataElement(map);
	}

	public void visitListPreIteration(MetadataList list) {}

	public void visitListPostIteration(MetadataList list) {
		visitMetadataElement(list);
	}

	public void visitField(MetadataField field) {
		visitMetadataElement(field);
	}

	public void visitArray(MetadataArray array) {
		visitMetadataElement(array);
	}

	public void visitKey(MetadataKey key) {
		visitMetadataElement(key);
	}

	protected void visitMetadataElement(MetadataElement ele) {}

}

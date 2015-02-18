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

package com.google.mr4c.serialize.bean.dataset;

import com.google.mr4c.dataset.DataFile;

public class DataFileBean {

	private String contentType;

	public static DataFileBean instance(DataFile file) {
		DataFileBean bean = new DataFileBean();
		bean.contentType = file.getContentType();
		return bean;
	}

	public DataFileBean(){}

	public DataFile toDataFile() {
		DataFile file = new DataFile(
			contentType
		);
		return file;
	}

}


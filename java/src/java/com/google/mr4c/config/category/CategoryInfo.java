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

package com.google.mr4c.config.category;

public interface CategoryInfo {

	String getCategoryName();

	/**
	  * system property that has a config file list
	*/
	String getCategoryProperty();

	/**
	  * prefix to use when properties are specified as command line arguments
	*/
	String getArgsPrefix();

	/**
	  * prefix when property is included as a namespaced property
	*/
	String getPropertiesPrefix();

	/**
	  * environment variable that has a config file list
	*/
	String getEnvironmentVariable();

	/**
	  * true if default category with no argument prefix required
	*/
	public boolean isDefaultCategory();

}

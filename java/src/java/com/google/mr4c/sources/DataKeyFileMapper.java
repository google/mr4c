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

package com.google.mr4c.sources;

import com.google.mr4c.keys.DataKey;

public interface DataKeyFileMapper {

	String getFileName(DataKey key);

	DataKey getKey(String name);

	boolean canMapName(String name);

	boolean canMapKey(DataKey key);

	// NOTE: Adding getDescription() might be helpful for logging.
	// Descriptions could include things like pattern to match, etc.

}


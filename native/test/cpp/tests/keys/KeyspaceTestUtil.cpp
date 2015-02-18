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

#include "keys/keys_api.h"
#include "keys/KeyspaceTestUtil.h"

namespace MR4C {

KeyspaceTestUtil::KeyspaceTestUtil() {

	m_dim1 = DataKeyDimension("dim1");
	m_dim2 = DataKeyDimension("dim2");
	m_dim3 = DataKeyDimension("dim3");

	m_ele1a = DataKeyElement("ele1a", m_dim1);
	m_ele1b = DataKeyElement("ele1b", m_dim1);
	m_ele1c = DataKeyElement("ele1c", m_dim1);
	m_ele2a = DataKeyElement("ele2a", m_dim2);
	m_ele2b = DataKeyElement("ele2b", m_dim2);
	m_ele3 = DataKeyElement("ele3", m_dim3);


}

Keyspace* KeyspaceTestUtil::buildKeyspace1() {

	std::vector<DataKeyElement> eles1;
	std::vector<DataKeyElement> eles2;
	std::vector<DataKeyElement> eles3;
		
	eles1.push_back(m_ele1a);
	eles1.push_back(m_ele1b);
	eles1.push_back(m_ele1c);

	eles2.push_back(m_ele2a);
	eles2.push_back(m_ele2b);

	eles3.push_back(m_ele3);

	std::set<KeyspaceDimension> dims;
	dims.insert(KeyspaceDimension(m_dim1,eles1));
	dims.insert(KeyspaceDimension(m_dim2,eles2));
	dims.insert(KeyspaceDimension(m_dim3,eles3));

	return new Keyspace(dims);

}

Keyspace* KeyspaceTestUtil::buildKeyspace2() {

	std::vector<DataKeyElement> eles1;
	std::vector<DataKeyElement> eles2;
	std::vector<DataKeyElement> eles3;
		
	eles1.push_back(m_ele1a);
	eles1.push_back(m_ele1b);
	// left out 1c to get a different keyspace

	eles2.push_back(m_ele2a);
	eles2.push_back(m_ele2b);

	eles3.push_back(m_ele3);

	std::set<KeyspaceDimension> dims;
	dims.insert(KeyspaceDimension(m_dim1,eles1));
	dims.insert(KeyspaceDimension(m_dim2,eles2));
	dims.insert(KeyspaceDimension(m_dim3,eles3));

	return new Keyspace(dims);

}

}


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

#ifndef __MR4C_PROPERTIES_H__
#define __MR4C_PROPERTIES_H__

#include <string>
#include <set>
#include <map>

namespace MR4C {

class PropertiesImpl;

/**
  * Replicates some of the functionality of Java's Properties class
*/

class Properties {

	public:

		Properties();

		Properties(const std::map<std::string,std::string>& propMap);
		Properties(const Properties& props);

		std::set<std::string> getAllPropertyNames() const;

		std::map<std::string,std::string> getPropertyMap() const;

		bool hasProperty(const std::string& name) const;

		std::string getProperty(const std::string& name) const;

		void setProperty(const std::string& name, const std::string& value);

		void setAllProperties(const Properties& props);

		void setAllProperties(const std::map<std::string,std::string>& propMap);

		bool operator==(const Properties& props) const;

		bool operator!=(const Properties& props) const;

		Properties& operator=(const Properties& props);

		~Properties();

	private:

		PropertiesImpl* m_impl;

};

}

#endif

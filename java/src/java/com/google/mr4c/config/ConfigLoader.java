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

package com.google.mr4c.config;

import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;

public class ConfigLoader {

	private ConfigDescriptor m_conf;
	private String m_desc;
	private Logger m_log;

	public ConfigLoader(
		ConfigDescriptor conf,
		String desc,
		Logger log
	) {
		m_conf = conf;
		m_desc = desc;
		m_log = log;
	}


	public Reader load() throws IOException {
		if ( m_conf.hasFile() ) {
			return loadFromFile();
		} else if ( m_conf.hasInline() ) {
			return loadInline();
		} else if ( m_conf.hasName() ) {
			throw new IllegalArgumentException("Named configs not supported yet");
		} else {
			throw new IllegalArgumentException("ConfigDescriptor is empty");
		}
	}
	
	private Reader loadFromFile() throws IOException {
		m_log.info("Loading {} from [{}]", m_desc, m_conf.getConfigFile());
		return loadFromContent();
	}

	private Reader loadInline() throws IOException {
		m_log.info("{} inline", m_desc);
		return loadFromContent();
	}

	private Reader loadFromContent() throws IOException {
		return m_conf.getContent();
	}

}

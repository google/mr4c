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

import com.google.mr4c.content.ContentFactories;

import java.io.IOException;
import java.io.StringReader;
import java.io.Reader;
import java.net.URI;

public class ConfigDescriptor {

	private String name;
	private URI file;
	private Document inline;

	// for gson usage
	private ConfigDescriptor() {}

	// strictly for testing purposes - gson can create invalid descriptor
	/*package*/ static ConfigDescriptor createInvalid(String name, URI file, Document inline) {
		ConfigDescriptor desc = new ConfigDescriptor();
		desc.name = name;
		desc.file = file;
		desc.inline = inline;
		return desc;
	}

	public ConfigDescriptor(String name) {
		if ( name==null ) {
			throw new IllegalArgumentException("name can't be null");
		}
		this.name = name;
	}

	public ConfigDescriptor(URI file) {
		if ( file==null ) {
			throw new IllegalArgumentException("file can't be null");
		}
		this.file = file;
	}

	public ConfigDescriptor(Document inline) {
		if ( inline==null ) {
			throw new IllegalArgumentException("inline content can't be null");
		}
		this.inline = inline;
	}

	public String getConfigName() {
		return this.name;
	}

	public URI getConfigFile() {
		return this.file;
	}

	public Document getInlineConfig() {
		return this.inline;
	}

	public boolean hasName() {
		return this.name!=null;
	}

	public boolean hasFile() {
		return this.file!=null;
	}

	public boolean hasInline() {
		return this.inline!=null;
	}

	public boolean isValid() {
		int total=0;
		if ( hasName() ) total++;
		if ( hasFile() ) total++;
		if ( hasInline() ) total++;
		return total==1;
	}

	public boolean hasContent() {
		return hasFile() || hasInline();
	}

	public Reader getContent() throws IOException {
		if ( hasFile() ) {
			return ContentFactories.readContentAsReader(file);
		} else if ( hasInline() ) {
			return new StringReader(inline.getContentAsString());
		} else {
			throw new IllegalStateException("No content available");
		}
	}

	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !obj.getClass().equals(this.getClass()) ) return false;
		ConfigDescriptor config = (ConfigDescriptor) obj;
		if ( !safeCompare(name, config.name) ) return false;
		if ( !safeCompare(file, config.file) ) return false;
		if ( !safeCompare(inline, config.inline) ) return false;
		return true; 
	}

	private boolean safeCompare(Object obj1, Object obj2 ) {
		if ( obj1==null && obj2==null ) {
			return true;
		} else if ( obj1==null || obj2==null ) {
			return false;
		} else {
			return obj1.equals(obj2);
		}
	}

	public int hashCode() {
		int hash=0;
		if ( hasName() ) hash += name.hashCode();
		if ( hasFile() ) hash += file.hashCode();
		if ( hasInline() ) hash += inline.hashCode();
		return hash;
	}

}

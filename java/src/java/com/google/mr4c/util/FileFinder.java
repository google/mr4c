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

package com.google.mr4c.util;

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.content.ContentFactories;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class FileFinder {
	
	public static interface FinderStep {
		URI find() throws IOException;
	}

	public static interface FileFilter {
		boolean filter(URI uri) throws IOException;
	}

	private static class ByProperty implements FinderStep {
		String m_prop;
		ByProperty(String prop) {
			m_prop=prop;
		}
		public URI find() throws IOException {
			String uri = MR4CConfig.getDefaultInstance().getCategory(Category.CORE).getProperty(m_prop);
			if ( StringUtils.isEmpty(uri) ) {
				return null;
			}
			return findFromURI(uri);
		}
	}

	private static class ByEnv implements FinderStep {
		String m_var;
		ByEnv(String var) {
			m_var=var;
		}
		public URI find() throws IOException {
			String uri = System.getenv(m_var);
			if ( StringUtils.isEmpty(uri) ) {
				return null;
			}
			return findFromURI(uri);
		}
	}

	private static class ByLocalFile implements FinderStep {
		String m_name;
		ByLocalFile(String name) {
			m_name=name;
		}

		public URI find() throws IOException {
			File file = new File(m_name);
			return file.exists() ? file.toURI() : null;
		}
	}

	private static class ByURI implements FinderStep {
		URI m_uri;
		ByURI(String uri) {
			this(URI.create(uri));
		}
		ByURI(URI uri) {
			m_uri=uri;
		}
		public URI find() throws IOException {
			return findFromURI(m_uri);
		}
	}

	private static class AlwaysPass implements FileFilter {
		public boolean filter(URI uri) {
			return true;
		}
	}


	private List<FinderStep> m_steps = new ArrayList<FinderStep>();
	private FileFilter m_filter = new AlwaysPass();


	public void addCustomFinderStep(FinderStep step) {
		m_steps.add(step);
	}

	public void addPropertyStep(String prop) {
		m_steps.add(new ByProperty(prop));
	}

	public void addEnvStep(String var) {
		m_steps.add(new ByEnv(var));
	}

	public void addLocalFileStep(String fileName) {
		m_steps.add(new ByLocalFile(fileName));
	}

	public void addURIStep(String uri) {
		m_steps.add(new ByURI(uri));
	}

	public void addURIStep(URI uri) {
		m_steps.add(new ByURI(uri));
	}

	public void setFilter(FileFilter filter) {
		m_filter = filter;
	}

	public URI findFile() throws IOException {
		for (FinderStep step : m_steps ) {
			URI uri = step.find();
			if ( uri==null ) {
				continue;
			}
			if ( m_filter.filter(uri) ) {
				return uri;
			}
		}
		return null;
	}

	
	private static URI findFromURI(String uri) throws IOException {
		return findFromURI(URI.create(uri));
	}
		
	private static URI findFromURI(URI uri) throws IOException {
		uri = ContentFactories.scrubURI(uri);
		return ContentFactories.exists(uri) ? uri : null;
	}
		
}

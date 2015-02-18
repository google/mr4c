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

import com.google.mr4c.config.execution.LocationsConfig;
import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.content.RelativeContentFactory;
import com.google.mr4c.util.MR4CLogging;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.hadoop.fs.Path;

import org.slf4j.Logger;

public abstract class FileSources {

	protected static final Logger s_log = MR4CLogging.getLogger(FileSources.class);

	private static Map<String,Factory> s_factories = new HashMap<String,Factory>();

	public static FileSource getFileSource(LocationsConfig locations) {
		if ( locations.hasMap() ) {
			return new HeterogenousFileSource(locations.getMap());
		} else if ( locations.hasList() ) {
			List<FileSource> sources = new ArrayList<FileSource>();
			for ( URI loc : locations.getList() ) {
				sources.add(getFileSource(loc));
			}
			return new AggregateFileSource(sources);
		} else {
			throw new IllegalStateException("No content in locations config");
		}
	}
	
	public static FileSource getFileSource(URI uri) {
		return getFileSource(uri, false);
	}

	public static FileSource getFileSource(URI uri, boolean flat) {
		s_log.info("Creating file source for [{}]", uri);
		uri = ContentFactories.scrubURI(uri);
		String scheme = uri.getScheme();
		Factory factory = s_factories.get(scheme);
		if ( factory==null ) {
			throw new IllegalArgumentException("No file source for scheme=["+scheme+"]");
		}
		return factory.create(uri, flat);
	}

	private static interface Factory {
		
		FileSource create(URI uri, boolean flat);

	}

	private static class LocalFactory implements Factory {

		public FileSource create(URI uri, boolean flat) {
			File file = new File(uri);
			s_log.info("Creating local file source for directory [{}]", file);
			return new DiskFileSource(file, flat);
		}

	}
	static { s_factories.put("file", new LocalFactory()); }
		
	private static class RelativeFactory implements Factory {

		public FileSource create(URI uri, boolean flat) {
			File file = RelativeContentFactory.toFile(uri);
			s_log.info("Creating relative file source for directory [{}]", file);
			return new DiskFileSource(file, flat);
		}

	}
	static { s_factories.put("rel", new RelativeFactory()); }
		
	private static class HDFSFactory implements Factory {

		private String m_desc;

		private HDFSFactory(String desc) {
			m_desc = desc;
		}

		public FileSource create(URI uri, boolean flat) {
			try {
				Path path = ContentFactories.toPath(uri);
				s_log.info("Creating {} file source for directory [{}]", m_desc, path);
				return HDFSFileSource.create(path, flat);
			} catch (IOException ioe) {
				throw new IllegalArgumentException(ioe);
			}
		}

	}
	static { s_factories.put("hdfs", new HDFSFactory("HDFS")); }

	private static class S3Factory implements Factory {

		private String m_desc;

		private S3Factory(String desc) {
			m_desc = desc;
		}

		public FileSource create(URI uri, boolean flat) {
			try {
				Path path = ContentFactories.toPath(uri);
				s_log.info("Creating {} file source for [{}]", m_desc, uri);
				return S3FileSource.create(uri, flat);
			} catch (IOException ioe) {
				throw new IllegalArgumentException(ioe);
			}
		}

	}
	static { s_factories.put("s3", new S3Factory("S3 block file system")); }
	static { s_factories.put("s3n", new S3Factory("S3 native")); }
		
}


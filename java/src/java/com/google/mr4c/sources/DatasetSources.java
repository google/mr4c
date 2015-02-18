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

import com.google.mr4c.config.ConfigDescriptor;
import com.google.mr4c.config.execution.DatasetConfig;
import com.google.mr4c.config.execution.LocationsConfig;
import com.google.mr4c.config.execution.MapConfig;
import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.content.HDFSContentFactory;
import com.google.mr4c.content.LocalContentFactory;
import com.google.mr4c.content.RelativeContentFactory;
import com.google.mr4c.serialize.DatasetSerializer;
import com.google.mr4c.serialize.SerializerFactories;
import com.google.mr4c.util.MR4CLogging;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;

import org.apache.hadoop.fs.Path;

import org.slf4j.Logger;

public abstract class DatasetSources {

	protected static final Logger s_log = MR4CLogging.getLogger(DatasetSources.class);

	private static Map<String,Factory> s_factories = new HashMap<String,Factory>();

	public static DatasetSource getDatasetSource(DatasetConfig config) throws IOException {
		if ( config==null ) {
			return new NullDatasetSource();
		}
		String scheme = config.getScheme();
		Factory factory = s_factories.get(scheme);
		if ( factory==null ) {
			throw new IllegalArgumentException("No dataset source for scheme=["+scheme+"]");
		}
		DatasetSource src = factory.create(config);
		src.setQueryOnly(config.getQueryOnly());
		s_log.info("Created dataset source [{}]", src.getDescription());
		ConfigDescriptor mapConf = config.getMapConfig();
		if ( mapConf!=null ) {
			src = transformSource(src, mapConf);
		}
		return src;
	}

	private static FileSource extractFileSource(DatasetConfig config) throws IOException {
		// multiple locations will override a single location
		if ( config.getLocations()!=null ) {
			LocationsConfig locConf = SourceLocationsConfig.load(config.getLocations());
			return FileSources.getFileSource(locConf);
		} else if ( config.getLocation()!=null ) {
			return FileSources.getFileSource(config.getLocation());
		} else {
			throw new IllegalArgumentException("No location info provided for dataset source");
		}
	}
	
	private static DatasetSource transformSource(DatasetSource src, ConfigDescriptor config) throws IOException {
		s_log.info("Wrapping transformation around dataset source [{}]", src.getDescription());
		MapConfig mapConfig = TransformedDatasetSourceConfig.load(config);
		return new TransformedDatasetSource(src, mapConfig);
	}

	private static interface Factory {
		
		DatasetSource create(DatasetConfig config) throws IOException;

	}

	private static class DirectoryFactory implements Factory {

		public DatasetSource create(DatasetConfig config) throws IOException {
			FileSource fileSrc = extractFileSource(config);
			if ( config.getSourceConfig()==null ) {
				return new FilesDatasetSource(fileSrc);
			} else {
				FilesDatasetSourceConfig srcConfig =  FilesDatasetSourceConfig.load(config.getSourceConfig());
				return new FilesDatasetSource(srcConfig, fileSrc);
			}
		}


	}
	static { s_factories.put("directory", new DirectoryFactory()); }
		
	private static class SimpleFactory implements Factory {

		public DatasetSource create(DatasetConfig config) throws IOException {
			URI file = config.getLocation();
			DatasetSerializer serializer = SerializerFactories.getSerializerFactory("application/json").createDatasetSerializer();

			return new SimpleDatasetSource(file, serializer);
		}

	}
	static { s_factories.put("simple", new SimpleFactory()); }
		
	private static class BinaryFactory implements Factory {

		public DatasetSource create(DatasetConfig config) throws IOException {
			URI file = config.getLocation();
			return new BinaryDatasetSource(file);
		}

	}
	static { s_factories.put("binary", new BinaryFactory()); }
		
	private static class MapFileFactory implements Factory {

		public DatasetSource create(DatasetConfig config) throws IOException {
			Path path  = ContentFactories.toPath(config.getLocation());
			ArchiveSource actualFileSrc = new MapFileSource(path);
			FilesDatasetSourceConfig srcConfig =  FilesDatasetSourceConfig.load(config.getSourceConfig());
			DatasetSource actualSrc = new ArchiveDatasetSource(srcConfig, actualFileSrc);
			if ( config.getStageLocation()==null ) {
				return actualSrc;
			}

			FileSource stageFileSrc = FileSources.getFileSource(config.getStageLocation());
			DatasetSource stageSrc = new FilesDatasetSource(srcConfig, stageFileSrc);
			return new StagedDatasetSource(actualSrc, stageSrc);
		}

	}
	static { s_factories.put("mapfile", new MapFileFactory()); }

	private static class MetafilesFactory implements Factory {

		public DatasetSource create(DatasetConfig config) throws IOException {
			FileSource fileSrc = extractFileSource(config);
			return new MetafilesDatasetSource(fileSrc);
		}

	}
	static { s_factories.put("metafiles", new MetafilesFactory()); }
		
	private static class LogsFactory implements Factory {

		public DatasetSource create(DatasetConfig config) throws IOException {
			FileSource fileSrc = FileSources.getFileSource(config.getLocation(), false);
			return LogsDatasetSource.create(fileSrc);
		}

	}
	static { s_factories.put("logs", new LogsFactory()); }
		
	private static class MBTilesFactory implements Factory {

		public DatasetSource create(DatasetConfig config) throws IOException {
			URI file = config.getLocation();

			return new MBTilesDatasetSource(file);
		}

	}
	static { s_factories.put("mbtiles", new MBTilesFactory()); }
		
}


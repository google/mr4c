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

import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.sources.DatasetSource.WriteMode;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public abstract class SourceUtils {

	public static void copySource(DatasetSource src, DatasetSource dest) throws IOException {
		Dataset dataset = src.readDataset();
		dest.writeDataset(dataset,WriteMode.ALL);
		dest.copyToFinal();
	}

	public static void copySource(FileSource src, FileSource dest, boolean streamOutput) throws IOException {
		for ( String file : src.getAllFileNames() ) {
			copyFile(src, file, dest, file, streamOutput);
		}
		dest.close();
		src.close();
	}

	public static void copySource(ArchiveSource src, ArchiveSource dest, boolean streamOutput) throws IOException {
		List<String> files  = new ArrayList<String>(src.getAllFileNames());
		Collections.sort(files);
		dest.startWrite();
		for ( String file : files ) {
			DataFileSource fileSrc = src.getFileSource(file);
			DataFileSink fileSink = dest.getFileSink(file);
			copyFile(fileSrc, fileSink, streamOutput);
		}
		for ( String file : src.getAllMetadataFileNames() ) {
			DataFileSource fileSrc = src.getMetadataFileSource(file);
			DataFileSink fileSink = dest.getMetadataFileSink(file);
			copyFile(fileSrc, fileSink, streamOutput);
		}
		dest.finishWrite();
		src.close();
	}

	public static void copySource(ArchiveSource src, FileSource dest, boolean streamOutput) throws IOException {
		for ( String file : src.getAllFileNames() ) {
			DataFileSource fileSrc = src.getFileSource(file);
			DataFileSink fileSink = dest.getFileSink(file);
			copyFile(fileSrc, fileSink, streamOutput);
		}
		for ( String file : src.getAllMetadataFileNames() ) {
			DataFileSource fileSrc = src.getMetadataFileSource(file);
			DataFileSink fileSink = dest.getFileSink(file);
			copyFile(fileSrc, fileSink, streamOutput);
		}
		src.close();
		dest.close();
	}

	public static void copyFile(FileSource src, String srcName,  FileSource dest, String destName, boolean streamOutput) throws IOException {
		DataFileSource fileSrc = src.getFileSource(srcName);
		DataFileSink fileSink = dest.getFileSink(destName);
		copyFile(fileSrc, fileSink, streamOutput);
	}

	private static void copyFile( DataFileSource fileSrc, DataFileSink fileSink, boolean streamOutput ) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = fileSrc.getFileInputStream();
			if ( streamOutput ) {
				output = fileSink.getFileOutputStream();
				IOUtils.copy(input,output);
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(input, baos);
				fileSink.writeFile(baos.toByteArray());
			}
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
	}

	public static Map<DatasetSource.SourceType,Set<String>> sortSourceNamesByType(Map<String,DatasetSource> srcMap) {
		Map<DatasetSource.SourceType,Set<String>> result = new HashMap<DatasetSource.SourceType,Set<String>>();
		for ( String name : srcMap.keySet() ) {
			DatasetSource src = srcMap.get(name);
			DatasetSource.SourceType type = src.getSourceType();
			Set<String> names = result.get(type);
			if ( names==null ) {
				names = new HashSet<String>();
				result.put(type, names);
			}
			names.add(name);
		}
		return result;
	}
		
}


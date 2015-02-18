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

import com.google.mr4c.dataset.LogsDatasetBuilder;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.util.MR4CLogging;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;

public class LogsDatasetSource extends FilesDatasetSource {

	public static final String ZIP_FILE_NAME = "logs.zip";

	protected static final Logger s_log = MR4CLogging.getLogger(LogsDatasetSource.class);

	private String m_desc;

	public static LogsDatasetSource create(FileSource fileSrc) {
		FilesDatasetSourceConfig config = buildConfig();
		return new LogsDatasetSource(config, fileSrc);
	}

	protected LogsDatasetSource(FilesDatasetSourceConfig config, FileSource fileSrc) {
		super(s_log, config, fileSrc);
		m_desc = String.format("logs dataset source stored by [%s]", fileSrc.getDescription());
	}

	public synchronized void copyToFinal() throws IOException {
		List<String> names = new ArrayList<String>(m_fileSrc.getAllFileNames());
		names.remove(ZIP_FILE_NAME);
		DataFileSink zipSink = m_fileSrc.getFileSink(ZIP_FILE_NAME);
		OutputStream output = zipSink.getFileOutputStream();
		try {
			ZipOutputStream zipStream = new ZipOutputStream(output);
			for ( String name : names ) {
				addZipEntry(zipStream, name);
			}
			zipStream.finish();
		} finally {
			output.close();
		}
	} 

	private void addZipEntry(ZipOutputStream zipStream, String name) throws IOException {
		ZipEntry entry = new ZipEntry(name);
		zipStream.putNextEntry(entry);
		DataFileSource entrySource = m_fileSrc.getFileSource(name);
		InputStream input = entrySource.getFileInputStream();
		try {
			IOUtils.copy(input, zipStream);
			zipStream.closeEntry();
		} finally {
			input.close();
		}
	}

	public String getDescription() {
		return m_desc;
	}

	private static FilesDatasetSourceConfig buildConfig() {
		FilesDatasetSourceConfig  config = new FilesDatasetSourceConfig();
		config.setSelfConfig(false);
		config.setIgnoreExtraFiles(true);

		CompositeKeyFileMapper mapper = new CompositeKeyFileMapper();
		mapper.addMapper(new PatternKeyFileMapper("logs/${TASKID}/${NAME}", LogsDatasetBuilder.NAME, LogsDatasetBuilder.TASKID)); // want to try this one first
		mapper.addMapper(new PatternKeyFileMapper("logs/${NAME}", LogsDatasetBuilder.NAME));

		config.setKeyFileMapper(mapper);
		return config;
	}

	public SourceType getSourceType() {
		return SourceType.LOGS;
	}

}

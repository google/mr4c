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

import com.google.mr4c.util.MR4CLogging;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import org.slf4j.Logger;

public class MapFileSource implements ArchiveSource {

	// at any time, either we can read the file, or we are writing it
	private static enum Mode {
		NONE, // no info about the file loaded yet
		READ, // index loaded, ready to read
		WRITE // in the process of writing
	};

	protected static final Logger s_log = MR4CLogging.getLogger(MapFileSource.class); 

	private Path m_dir;
	private Path m_dataPath;
	private Path m_indexPath;
	private Path m_metaPath;
	private String m_dirStr; // Unfortunately, MapFile constructors take a String instead of a Path
	private FileSystem m_fs;
	private FileStatus m_status;
	private Configuration m_config;
	private MapFile.Reader m_reader;
	private MapFile.Writer m_writer;
	private MapFileIndex m_index;
	private Mode m_mode = Mode.NONE;

	public MapFileSource(Path dir) throws IOException {
		this(dir.getFileSystem(new Configuration()), dir);
	}

	public MapFileSource(FileSystem fs, Path dir) throws IOException {
		m_fs = fs;
		m_config = m_fs.getConf();
		Path root = new Path(fs.getUri());
		m_dir = new Path(root, dir);
		m_dirStr = m_dir.toUri().getPath();
		m_dataPath = new Path(m_dir,MapFile.DATA_FILE_NAME);
		m_indexPath = new Path(m_dir,MapFile.INDEX_FILE_NAME);
		m_metaPath = new Path(m_dir,"metadata");
	}

	public synchronized List<String> getAllFileNames() throws IOException {
		ensureRead();
		return m_index.getFileNames();
	}

	public synchronized List<String> getAllMetadataFileNames() throws IOException {
		ensureRead();
		if ( !m_fs.exists(m_metaPath) ) {
			return Collections.emptyList();
		}
		List<String> names = new ArrayList<String>();
		for ( FileStatus status : m_fs.listStatus(m_metaPath) ) {
			names.add(status.getPath().getName());
		}
		return names;
	}

	public synchronized DataFileSource getFileSource(String fileName) throws IOException {
		ensureRead();
		long offset = m_index.getOffset(fileName);
		long length = m_index.getLength(fileName);
		return new MapFileDataFileSource(fileName, offset, length);
	}

	public synchronized boolean fileExists(String fileName) throws IOException {
		ensureRead();
		return m_index.fileExists(fileName);
	}

	public DataFileSource getFileSourceOnlyIfExists(String fileName) throws IOException {
		return fileExists(fileName) ?
			getFileSource(fileName) :
			null;
	}

	public DataFileSource getMetadataFileSource(String fileName) throws IOException {
		return new URIDataFileSource(toURI(fileName), fileName);
	}

	public DataFileSink getFileSink(String fileName) throws IOException {
		return new MapFileDataFileSink(fileName);
	}

	public DataFileSink getMetadataFileSink(String fileName) throws IOException {
		return new URIDataFileSink(toURI(fileName), fileName);
	}

	private URI toURI(String fileName) throws IOException {
		Path path = new Path(m_metaPath, fileName);
		return path.toUri();
	}

	public synchronized void clear() throws IOException {
		assertNotWrite();
		cleanupRead();
		m_fs.delete(m_dataPath, false);
		m_fs.delete(m_indexPath, false);
		m_fs.delete(m_metaPath, true);
	}

	public synchronized boolean exists() throws IOException {
		boolean indexExists = m_fs.exists(m_indexPath);
		boolean dataExists = m_fs.exists(m_dataPath);
		
		if ( indexExists && dataExists ) {
			return true; // complete map file exists
		} else if ( dataExists ) {
			// NOTE: can't just fix the index, no control over how many entries
			throw new FileNotFoundException("Found only data file for " + getDescription());
		} else if ( indexExists ) {
			throw new FileNotFoundException("Found only index file for " + getDescription());
		} else {
			return false;
		}
	}

	public String getDescription() {
		return "map file: " + m_dir.toUri().toString();
	}

	private String getFileDescription(String fileName) {
		return getDescription() + " file " + fileName;
	}

	private void ensureRead() throws IOException {
		switch(m_mode) {
			case READ :
				return;
			case WRITE :
				throw new IOException("Tried to read in the middle of a write");
			default : 
				initRead();
		}
	}

	private void assertWrite() throws IOException {
		assertWrite(null);
	}

	private void assertWrite(String msg) throws IOException {
		if ( m_mode!=Mode.WRITE ) {
			String error = "Assert write failed for " + getDescription();
			if ( msg!=null ) {
				error += ("; " + msg);
			}
			throw new IOException(error);
		}
	}

	private void assertNotWrite() throws IOException {
		assertNotWrite(null);
	}

	private void assertNotWrite(String msg) throws IOException {
		if ( m_mode==Mode.WRITE ) {
			String error = "Assert not write failed for " + getDescription();
			if ( msg!=null ) {
				error += ("; " + msg);
			}
			throw new IOException(error);
		}
	}

	private void assertExists() throws IOException {
		if ( !m_fs.exists(m_dataPath) ) {
			throw new FileNotFoundException("No data file found for " + getDescription());
		}
		if ( !m_fs.exists(m_indexPath) ) {
			throw new FileNotFoundException("No index file found for " + getDescription());
		}
	}

	private synchronized void initRead() throws IOException {
		if ( m_mode==Mode.READ) {
			return;
		}
		assertExists();
		m_index = new MapFileIndex();
		m_status = m_fs.getFileStatus(m_dataPath);
		m_reader = new MapFile.Reader(m_fs, m_dirStr, m_config);
		m_mode = Mode.READ;
	}

	private synchronized void cleanupRead() throws IOException {
		if ( m_mode!=Mode.READ) {
			return;
		}
		closeReader();
		m_index=null;
		m_status=null;
		m_mode = Mode.NONE;
	}

	public synchronized void startWrite() throws IOException {
		assertNotWrite("Tried to restart write");
		cleanupRead();
		m_writer = new MapFile.Writer(m_config, m_fs, m_dirStr, Text.class, BytesWritable.class);
		m_writer.setIndexInterval(1);
		m_fs.mkdirs(m_metaPath);
		m_mode = Mode.WRITE;
	}

	public synchronized void finishWrite() throws IOException {
		assertWrite("Tried to finish non-existent write");
		closeWriter();
		m_mode = Mode.NONE;
	}

	private synchronized MapFile.Reader getReader() throws IOException {
		ensureRead();
		return m_reader;
	}

	private synchronized MapFile.Writer getWriter() throws IOException {
		assertWrite();
		return m_writer;
	}

	public synchronized void close() throws IOException {
		assertNotWrite();
		closeReader();
	}

	private synchronized void closeReader() throws IOException {
		if ( m_reader!=null ) {
			m_reader.close();
		}
	}

	private synchronized void closeWriter() throws IOException {
		if ( m_writer!=null ) {
			m_writer.close();
		}
	}

	private class MapFileDataFileSource extends AbstractDataFileSource {
	
	
		private DataFileSource m_cache;
		private String m_name;
		private long m_offset;
		private long m_length;
	
		private MapFileDataFileSource( String name, long offset, long length) {
			m_name = name;
			m_offset = offset;
			m_length = length;
		}

		public long getFileSize() throws IOException {
			// Length in the MapFile is not the same as the actual number of data bytes, so need to read it to find out
			loadBytesIfNecessary();
			return m_cache.getFileSize();
		}
	
		public InputStream getFileInputStream() throws IOException {
			// no stream support, probably should just use cache
			loadBytesIfNecessary();
			s_log.debug("Creating stream for reading file content cached from [{}]", getDescription());
			return m_cache.getFileInputStream();
		}
	
		public byte[] getFileBytes() throws IOException {
			loadBytesIfNecessary();
			return m_cache.getFileBytes();
		}
	
		private synchronized void loadBytesIfNecessary() throws IOException {
			if ( m_cache==null ) {
				s_log.debug("Reading file content from [{}]", getDescription());
				Text key = new Text(m_name);
				BytesWritable value = new BytesWritable();
				if ( getReader().get(key,value)==null ) {
					throw new IOException(String.format("[%s] not found", getDescription()));
				}
				byte[] bytes = Arrays.copyOfRange(value.getBytes(), 0, value.getLength() ); // pulling the whole array will get extra padding
				s_log.debug("Read {} bytes from [{}]", bytes.length, getDescription()); 
				m_cache = new BytesDataFileSource(bytes);
			}
		}

		public void getFileBytes(ByteBuffer buf) throws IOException {
			loadBytesIfNecessary();
			m_cache.getFileBytes(buf);
		}
			
	
		public void release() {
			if ( m_cache!=null ) {
				m_cache.release();
			}
			m_cache=null;
		}
	
		@Override public String getFileName() {
			return m_name;
		}
	
		public String getDescription() {
			return getFileDescription(m_name);
		}

		@Override public BlockLocation[] getBlockLocation() throws IOException {
			return m_fs.getFileBlockLocations(m_status, m_offset, m_length);
		}
	
	}


	private class MapFileDataFileSink extends AbstractDataFileSink {
	
		private String m_name;
	
		private MapFileDataFileSink(String name) {
			m_name = name;
		}
	
		public OutputStream getFileOutputStream() throws IOException {
			throw new UnsupportedOperationException("No way to stream data to a map file");
		}
	
		public void writeFile(byte[] bytes) throws IOException {
			s_log.debug("Writing {} bytes of file content to [{}]", bytes.length, getDescription());
			writeBytes(bytes);
		}
	
		public void writeFile(InputStream input) throws IOException {
			s_log.debug("Writing file content from stream to [{}]", getDescription());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(input, baos);
			writeBytes(baos.toByteArray());
		}

		private synchronized void writeBytes(byte[] bytes) throws IOException {
			BytesWritable data = new BytesWritable(bytes);
			Text key = new Text(m_name);
			getWriter().append(key,data);
		}

		@Override public String getFileName() {
			return m_name;
		}
	
		public String getDescription() {
			return getFileDescription(m_name);
		}

	}


	private class MapFileIndex {
	
		private List<String> m_names = new ArrayList<String>();
		private Map<String,Long> m_offsets = new HashMap<String,Long>();
		private Map<String,Long> m_lengths = new HashMap<String,Long>();
	
	
		private MapFileIndex() throws IOException {
			init();
		}
	
		public List<String> getFileNames() {
			return m_names;
		}

		public boolean fileExists(String fileName) {
			return m_names.contains(fileName);
		}
	
		public long getOffset(String fileName) {
			Long offset = m_offsets.get(fileName);
			if ( offset==null ) {
				throw new IllegalArgumentException(String.format("No file named [%s] in the index", fileName));
			}
			return offset;
		}
	
		public long getLength(String fileName) {
			Long length = m_lengths.get(fileName);
			if ( length==null ) {
				throw new IllegalArgumentException(String.format("No file named [%s] in the index", fileName));
			}
			return length;
		}
	
		private void init() throws IOException {
			SequenceFile.Reader reader = new SequenceFile.Reader(m_fs, m_indexPath, m_config);
			try {
				Text key = new Text();
				LongWritable val = new LongWritable();
				boolean first=true;
				String lastName=null;
				long lastOffset=0;
				while ( reader.next(key,val) ) {
					String name = key.toString();
					long offset = val.get();
					m_names.add(name);
					m_offsets.put(name,offset);
					if ( !first ) {
						long length = offset - lastOffset;
						m_lengths.put(lastName, length);
					}
					lastName = name;
					lastOffset = offset;
					first=false;
				}
				FileStatus status = m_fs.getFileStatus(m_dataPath);
				long length = status.getLen() - lastOffset;
				m_lengths.put(lastName, length);
			} finally {
				reader.close();
			}
		}
			
	}

}


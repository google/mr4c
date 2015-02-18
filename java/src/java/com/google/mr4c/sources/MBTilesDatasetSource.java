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

import com.google.mr4c.content.ContentFactories;
import com.google.mr4c.dataset.DataFile;
import com.google.mr4c.dataset.Dataset;
import com.google.mr4c.keys.DataKey;
import com.google.mr4c.keys.DataKeyDimension;
import com.google.mr4c.keys.DataKeyElement;
import com.google.mr4c.keys.DataKeyFactory;
import com.google.mr4c.mbtiles.MBTilesFile;
import com.google.mr4c.mbtiles.Tile;
import com.google.mr4c.mbtiles.TileFormat;
import com.google.mr4c.mbtiles.TileKey;
import com.google.mr4c.metadata.MetadataField;
import com.google.mr4c.metadata.MetadataMap;
import com.google.mr4c.metadata.PrimitiveType;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import org.apache.hadoop.fs.BlockLocation;


public class MBTilesDatasetSource extends AbstractDatasetSource  implements DatasetSource {

	public static final DataKeyDimension ZOOM_DIM = new DataKeyDimension("ZOOM");
	public static final DataKeyDimension X_DIM = new DataKeyDimension("X");
	public static final DataKeyDimension Y_DIM = new DataKeyDimension("Y");

	private static final DataKey ROOT_KEY = DataKeyFactory.newKey();

	private URI m_uri;
	private File m_file;
	private MBTilesFile m_mbtiles;
	private boolean m_staged;

	public MBTilesDatasetSource(URI uri) {
		m_uri = ContentFactories.scrubURI(uri);
		m_staged = !"file".equals(m_uri.getScheme());
	}

	// For in-memory mbtiles only
	public MBTilesDatasetSource(MBTilesFile mbtiles) {
		m_mbtiles = mbtiles;
	}

	public synchronized Dataset readDataset() throws IOException {
		ensureReadable();
		Dataset dataset = new Dataset();
		readMetadata(dataset);
		readDataFiles(dataset);
		m_mbtiles.close();
		return dataset;
	}

	private void ensureLocalFile() throws IOException {
		if ( m_file!=null ) {
			return;
		}
		m_file = m_staged ?
			File.createTempFile("mr4c", "mbtiles") :
			new File(m_uri);
	}

	private void copyToLocal() throws IOException {
		InputStream input=null;
		try {
			input = ContentFactories.readContentAsStream(m_uri);
			ContentFactories.writeContent(m_file.toURI(), input);
		} finally {
			if ( input!=null ) { 
				input.close();
			}
		}
	}

	private void ensureReadable() throws IOException {
		ensureLocalFile();
		if ( m_mbtiles!=null ) {
			return;
		}
		if ( m_staged ) {
			copyToLocal();
		}
		m_mbtiles = MBTilesFile.create(m_file, MBTilesFile.FileMode.READ_ONLY);
	}

	private void readMetadata(Dataset dataset) throws IOException {
		Map<String,String> mbMeta = m_mbtiles.getMetadataMap();
		MetadataMap bbMeta = toMetadata(mbMeta);
		dataset.addMetadata(ROOT_KEY, bbMeta);
	}

	private void readDataFiles(Dataset dataset) throws IOException {
		TileFormat format = m_mbtiles.getFormat(true);
		for ( TileKey tileKey : m_mbtiles.getAllTileKeys() ) {
			DataKey fileKey = toFileKey(tileKey); 
			DataFileSource fileSource = new MBTilesDataFileSource(tileKey);
			DataFile file = new DataFile(fileSource, format.getContentType());
			dataset.addFile(fileKey, file);
		}
	}

	public void writeDataset(Dataset dataset) throws IOException {
		writeDataset(dataset, WriteMode.ALL);
	}

	public synchronized void writeDataset(Dataset dataset, WriteMode writeMode) throws IOException {
		ensureWritable();
		if ( writeMode!=WriteMode.SERIALIZED_ONLY ) {
			writeDataFiles(dataset);
		}
		if ( writeMode!=WriteMode.FILES_ONLY ) {
			writeMetadata(dataset);
		}
		m_mbtiles.close();
	}

	private void ensureWritable() throws IOException {
		ensureLocalFile();
		if ( m_mbtiles!=null && m_mbtiles.getFileMode()!=MBTilesFile.FileMode.READ_ONLY) {
			return;
		}
		if ( m_mbtiles!=null ) {
			m_mbtiles.close();
		}
		m_mbtiles = MBTilesFile.create(m_file, MBTilesFile.FileMode.REPLACE);
	}

	private void writeMetadata(Dataset dataset) throws IOException {
		MetadataMap bbMeta = dataset.getMetadata(ROOT_KEY);
		Map<String,String> mbMeta = toMetadata(bbMeta);
		m_mbtiles.addMetadata(mbMeta);
		// NOTE: ignoring other metdata, might want an exception
	}

	private void writeDataFiles(Dataset dataset) throws IOException {
		for ( DataKey fileKey : dataset.getAllFileKeys() ) {
			DataFile file = dataset.getFile(fileKey);
			DataFileSink sink = getDataFileSink(fileKey);
			sink.writeFile(file.getBytes());
		}
	}
	
	public DataFile findDataFile(DataKey key) throws IOException {
		throw new UnsupportedOperationException("Find by key not currently supported for MBTiles source");
	}

	public DataFileSink getDataFileSink(DataKey key) throws IOException {
		ensureWritable();
		return new MBTilesDataFileSink(toTileKey(key));
	}

	public synchronized void copyToFinal() throws IOException {
		if ( m_mbtiles==null ) {
			return;
		}
		m_mbtiles.close();
		if ( m_staged ) {
			copyFromLocal();
		}
	}

	public synchronized void copyFromLocal() throws IOException {
		InputStream input=null;
		try {
			input = ContentFactories.readContentAsStream(m_file.toURI());
			ContentFactories.writeContent(m_uri, input);
		} finally {
			if ( input!=null ) { 
				input.close();
			}
		}
	}

	public void ensureExists() throws IOException {
		if ( m_file!=null ) {
			ContentFactories.ensureParentExists(m_file.toURI());
		} 
	}

	public String getDescription() {
		return m_file==null ?
			"In memory MBTiles file" :
			String.format("MBTiles file at %s", m_file);
	}


	private DataKey toFileKey(TileKey tileKey) {
		return DataKeyFactory.newKey(
			toElement(tileKey.getZoom(), ZOOM_DIM),
			toElement(tileKey.getX(), X_DIM),
			toElement(tileKey.getY(), Y_DIM)
		);
	}

	private DataKeyElement toElement(int val, DataKeyDimension dim) {
		return new DataKeyElement(Integer.toString(val), dim);
	}

	private TileKey toTileKey(DataKey fileKey) {
		return new TileKey(
			toInteger(fileKey, ZOOM_DIM),
			toInteger(fileKey, X_DIM),
			toInteger(fileKey, Y_DIM)
		);
	}

	private int toInteger(DataKey fileKey, DataKeyDimension dim) {
		DataKeyElement ele = fileKey.getElement(dim);
		return Integer.parseInt(ele.getIdentifier());
	}

	private MetadataMap toMetadata(Map<String,String> mbMeta) {
		MetadataMap bbMeta = new MetadataMap();
		for ( String name : mbMeta.keySet() ) {
			MetadataField field = new MetadataField(mbMeta.get(name), PrimitiveType.STRING);
			bbMeta.getMap().put(name, field);
		}
		return bbMeta;
	}

	private Map<String,String> toMetadata(MetadataMap bbMeta) {
		Map<String,String> mbMeta = new HashMap<String,String>();
		for ( String name : bbMeta.getMap().keySet() ) {
			MetadataField field = (MetadataField) bbMeta.getMap().get(name);
			String value = (String) field.getValue();
			mbMeta.put(name, value);
		}
		return mbMeta;
	}
			
	private class MBTilesDataFileSource extends AbstractDataFileSource {

		private TileKey m_key;
		private Tile m_tile;

		private MBTilesDataFileSource(TileKey key) {
			m_key = key;
		}

		public long getFileSize() throws IOException {
			loadTileIfNecessary();
			return m_tile.getData().length;
		}

		public InputStream getFileInputStream() throws IOException {
			byte[] bytes = m_tile==null ?
				m_mbtiles.findTile(m_key).getData() :
				m_tile.getData();
			return new ByteArrayInputStream(bytes);
		}

		public byte[] getFileBytes() throws IOException {
			loadTileIfNecessary();
			return m_tile.getData();
		}

		public void getFileBytes(ByteBuffer buf) throws IOException {
			loadTileIfNecessary();
			buf.put(m_tile.getData());
		}

		public void release() {
			m_tile = null;
		}

		public String getDescription() {
			return String.format("Tile for %s", m_key);
		}

		private synchronized void loadTileIfNecessary() throws IOException {
			if ( m_tile==null ) {
				m_tile = m_mbtiles.findTile(m_key);
			}
		}

	}

	private class MBTilesDataFileSink extends AbstractDataFileSink {

		private TileKey m_key;

		private MBTilesDataFileSink(TileKey key) {
			m_key = key;
		}


		public OutputStream getFileOutputStream() throws IOException {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			return new FilterOutputStream(baos) {
				public void close() throws IOException {
					writeFile(baos.toByteArray());
				}
			};
		}
					
		public synchronized void writeFile(byte[] bytes) throws IOException {
			Tile tile = new Tile(m_key, bytes);
			m_mbtiles.addTile(tile);
		}

		public void writeFile(InputStream input) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(input,baos);
			writeFile(baos.toByteArray());
		}

		public String getDescription() {
			return String.format("Tile for %s", m_key);
		}

	}

}



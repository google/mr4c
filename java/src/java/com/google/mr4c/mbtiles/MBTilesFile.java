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

package com.google.mr4c.mbtiles;

import com.google.mr4c.content.ContentFactories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MBTilesFile {

	public enum FileMode {READ_ONLY, UPDATE, REPLACE};

	public static final String CREATE_TILES_TABLE = 
		"CREATE TABLE tiles (" +
			"zoom_level integer NOT NULL," +
			"tile_column integer NOT NULL," +
			"tile_row integer NOT NULL," +
			"tile_data blob NOT NULL," +
			"PRIMARY KEY (zoom_level, tile_column, tile_row)" +
		")";

	public static final String CREATE_METADATA_TABLE = 
		"CREATE TABLE metadata (" +
			"name text NOT NULL," +
			"value text NOT NULL," +
			"PRIMARY KEY (name)" +
		")";

	public static final String INSERT_TILE = "insert into tiles (zoom_level, tile_column, tile_row, tile_data) values (?, ?, ?, ?)";

	public static final String FIND_TILE = "select tile_data from tiles where zoom_level=? and tile_column=? and tile_row=?";

	public static final String FIND_KEYS = "select zoom_level, tile_column, tile_row from tiles";

	public static final String FIND_METADATA = "select * from metadata";

	public static final String INSERT_METADATA = "insert into metadata (name, value) values (?, ?)";

	private boolean m_memory;
	private File m_file;
	private FileMode m_mode;
	private boolean m_init;
	private String m_url;
	private Connection m_conn;

	
	public static MBTilesFile createInMemory() throws IOException {
		MBTilesFile mbtiles = new MBTilesFile();
		mbtiles.init();
		return mbtiles;
	}

	private MBTilesFile() {
		m_memory = true;
		m_init = true;
		m_url = "jdbc:sqlite:";
	}

	public static MBTilesFile create(File file, FileMode mode) throws IOException {
		MBTilesFile mbtiles = new MBTilesFile(file, mode);
		mbtiles.init();
		return mbtiles;
	}

	private MBTilesFile(File file, FileMode mode) throws IOException {
		m_file = file;
		m_mode = mode;
		handleFile();
		m_url = "jdbc:sqlite:" + file.getPath();
	}

	private void handleFile() throws IOException {
		switch (m_mode) {
			case READ_ONLY :
				handleReadOnly();
				break;
			case UPDATE :
				handleUpdate();
				break;
			case REPLACE :
				handleReplace();
				break;
			default : ;// can't happen
		}
	}
			
	private void handleReadOnly() throws IOException {
		if ( !m_file.exists() ) {
			throw new FileNotFoundException(m_file.getPath());
		}
	}

	private void handleUpdate() throws IOException {
		if ( !m_file.exists() ) {
			handleNewFile();
		}
	}

	private void handleReplace() throws IOException {
		if ( m_file.exists() ) {
			if ( !m_file.delete() ) {
				throw new IOException("Couldn't delete file " + m_file);
			}
		}
		handleNewFile();
	}

	private void handleNewFile() throws IOException {
		ContentFactories.ensureParentExists(m_file.toURI());
		m_init = true;
	}
		
	private void init() throws IOException {
		ensureDriver();
		if ( m_init ) {
			initSchema();
		}
	}

	private synchronized void ensureConnection() throws IOException {
		if ( isClosed(m_conn) ) {
			m_conn = getConnection(m_url);
		}
	}

	private static boolean isClosed(Connection conn) throws IOException {
		try {
			return conn==null || conn.isClosed();
		} catch ( SQLException sqle ) {
			throw new IOException(sqle);
		} 
	}

	private static Connection getConnection(String url) throws IOException {
		try {
			return DriverManager.getConnection(url);
		} catch ( SQLException sqle ) {
			throw new IOException(sqle);
		} 
	}

	private static void ensureDriver() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			throw new IllegalStateException("Can't load SQLite JDBC driver", e);
		}
	}

	private void initSchema() throws IOException {
		ensureConnection();
		Statement stmt = null;
		try {
			stmt = m_conn.createStatement();
			stmt.executeUpdate(CREATE_TILES_TABLE);
			stmt.executeUpdate(CREATE_METADATA_TABLE);
		} catch ( SQLException sqle ) {
			throw new IOException(sqle);
		} finally {
			close(stmt);
		}
	}

	public void close() throws IOException {
		try {
			if ( m_conn!=null ) {
				m_conn.close();
			}
		} catch ( SQLException sqle ) {
			throw new IOException(sqle);
		} 
	}

	public boolean isInMemory() {
		return m_memory;
	}

	public File getFile() {
		return m_file;
	}

	public FileMode getFileMode() {
		return m_mode;
	}

	public void addTiles(Iterable<Tile> tiles) throws IOException {
		for ( Tile tile : tiles ) {
			addTile(tile);
		}
	}

	public void addTile(Tile tile) throws IOException {
		assertWritable();
		ensureConnection();
		PreparedStatement ps = null;
		try {
			ps = m_conn.prepareStatement(INSERT_TILE);
			TileKey key = tile.getKey();
			ps.setInt(1, key.getZoom());
			ps.setInt(2, key.getColumn());
			ps.setInt(3, key.getRow());
			ps.setBytes(4, tile.getData());
			ps.executeUpdate();
		} catch ( SQLException sqle ) {
			throw new IOException(sqle);
		} finally {
			close(ps);
		}
	}

	public Tile findTile(TileKey key) throws IOException {
		ensureConnection();
		PreparedStatement ps = null;
		try {
			ps = m_conn.prepareStatement(FIND_TILE);
			ps.setInt(1, key.getZoom());
			ps.setInt(2, key.getColumn());
			ps.setInt(3, key.getRow());
			ResultSet rs = ps.executeQuery();
			if ( !rs.next() ) {
				return null;
			}
			byte[] data = rs.getBytes("tile_data");
			return new Tile(key, data);
		} catch ( SQLException sqle ) {
			throw new IOException(sqle);
		} finally {
			close(ps);
		}
	}

	public Set<TileKey> getAllTileKeys() throws IOException {
		ensureConnection();
		PreparedStatement ps = null;
		try {
			ps = m_conn.prepareStatement(FIND_KEYS);
			ResultSet rs = ps.executeQuery();
			Set<TileKey> result = new HashSet<TileKey>();
			while ( rs.next() ) {
				int zoom = rs.getInt("zoom_level");
				int col = rs.getInt("tile_column");
				int row = rs.getInt("tile_row");
				result.add(new TileKey(zoom, col, row));
			}
			return result;
		} catch ( SQLException sqle ) {
			throw new IOException(sqle);
		} finally {
			close(ps);
		}
	}

	public Map<String,String> getMetadataMap() throws IOException {
		ensureConnection();
		PreparedStatement ps = null;
		try {
			ps = m_conn.prepareStatement(FIND_METADATA);
			ResultSet rs = ps.executeQuery();
			Map<String,String> result = new HashMap<String,String>();
			while ( rs.next() ) {
				String name = rs.getString("name");
				String value = rs.getString("value");
				result.put(name,value);
			}
			return result;
		} catch ( SQLException sqle ) {
			throw new IOException(sqle);
		} finally {
			close(ps);
		}
	}

	public void addMetadata(Map<String,String> data) throws IOException {
		ensureConnection();
		for ( String name : data.keySet() ) {
			String val = data.get(name);
			addMetadata(name, val);
		}
	}

	public void addMetadata(String name, String value) throws IOException {
		ensureConnection();
		assertWritable();
		PreparedStatement ps = null;
		try {
			ps = m_conn.prepareStatement(INSERT_METADATA);
			ps.setString(1, name);
			ps.setString(2, value);
			ps.executeUpdate();
		} catch ( SQLException sqle ) {
			throw new IOException(sqle);
		} finally {
			close(ps);
		}
	}

	public void setFormat(TileFormat format) throws IOException {
		addMetadata(TileFormat.METADATA_NAME, format.getName());
	}

	public TileFormat getFormat(boolean fallbackToDefault) throws IOException {
		TileFormat format = TileFormat.extractFromMetadata(getMetadataMap());
		return format!=null ?
			format :
			fallbackToDefault ?
				TileFormat.getDefaultFormat() :
				null;
	}

	private void close(Statement stmt) throws IOException {
		try {
			if ( stmt!=null ) {
				stmt.close();
			}
		} catch ( SQLException sqle ) {
			throw new IOException(sqle);
		}
	}

	private void assertWritable() {
		if ( m_mode==FileMode.READ_ONLY ) {
			throw new IllegalStateException("MBTiles file is read-only");
		}
	}

}

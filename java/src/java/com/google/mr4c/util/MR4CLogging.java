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

import com.google.mr4c.config.category.CoreConfig;
import com.google.mr4c.content.ContentFactories;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MR4CLogging {
		
	public static final String ROOT = "mr4c.java";
	public static final String JAVA_ALGORITHM_ROOT = "mr4c.javaalgo";
	public static final String CONF = "/etc/mr4c";
	public static final String PROP_IDENTIFY = "mr4c.logger.config";
	public static final String PROP_FILE = "mr4c.log4j";
	public static final String LOG4J_FILE = "log4j.properties";
	public static final String LOG4CXX_FILE = "log4cxx.properties";
	public static final String LOG4CXX_ENV = "MR4C_LOG4CXX_CONFIG";

	private FileFinder m_log4jFinder;
	private FileFinder m_log4cxxFinder;
	private FileFinder m_log4jFinderNoInstalled;
	private FileFinder m_log4cxxFinderNoInstalled;
	private FileFinder.FileFilter m_filter;

	private Logger m_logger;
	private boolean m_init;
	private boolean m_defaultInit;
	private boolean m_defaultInitMR4C;
	private boolean m_initMR4C;
	private URI m_file;

	private static MR4CLogging s_instance = new MR4CLogging();

	public static MR4CLogging instance() {
		return s_instance;
	}

	private MR4CLogging() {
		buildLog4jFileFinders();
		buildLog4cxxFileFinders();
		buildFileFilter();
	}

	public static Logger getLogger(Class clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String name) {
		s_instance.initLogging();
		return s_instance.getLoggerHelper(name);
	}

	private Logger getLoggerHelper(String name) {
		// strip off com...mr4c
		String logName = ROOT + name.replace("com.google.mr4c", "");
		return LoggerFactory.getLogger(logName);
	}

	/**
	 * Returns a logger that outputs into MR4C's algorithm logs.
	 * Should be used by any algorithms implemented in Java.
	 */
	public static Logger getAlgorithmLogger(Class clazz) {
		return getAlgorithmLogger(clazz.getName());
	}

	/**
	 * Returns a logger that outputs into MR4C's algorithm logs.
	 * Should be used by any algorithms implemented in Java.
	 */
	public static Logger getAlgorithmLogger(String name) {
		s_instance.initLogging();
		return s_instance.getAlgorithmLoggerHelper(name);
	}

	private Logger getAlgorithmLoggerHelper(String name) {
		// add algorithm root
		String logName = JAVA_ALGORITHM_ROOT + '.' + name;
		return LoggerFactory.getLogger(logName);
	}

	private synchronized void initLogging() {
		if ( m_init ) {
			return;
		}
		try {
			initLoggingHelper();
		} catch ( IOException ioe ) {
			throw new IllegalStateException(ioe);
		}
	}

	private void initLoggingHelper() throws IOException {
		checkLog4jInitialized();
		checkMR4CInitialized();
		loadMR4CIfNecessary();
		loadBasicConfIfNecessary();
		initLocalLogger();
		logResult();
		m_init=true;
	}

	private void checkLog4jInitialized() {
		// Log4j will try to find a way to initialize as soon as the Logger class loads
		// If there are no root appenders, then we should try to init
		m_defaultInit = org.apache.log4j.Logger.getRootLogger().getAllAppenders().hasMoreElements();
	}

	private void checkMR4CInitialized() {
		m_defaultInitMR4C = LogManager.exists(ROOT)!=null;
		m_initMR4C = m_defaultInitMR4C;
	}

	private void loadMR4CIfNecessary() throws IOException {
		if ( m_initMR4C ) {
			return;
		}
		URI file = findLog4jConfigFile(true);
		if ( file!=null ) {
			loadLog4jConfigFile(file);
			m_initMR4C=true;
			m_file = file;
		}
	}

	private void loadBasicConfIfNecessary() {
		if ( !m_initMR4C && !m_defaultInit ) {
			BasicConfigurator.configure();
		}
	}

	private void initLocalLogger() {
		m_logger = getLoggerHelper(MR4CLogging.class.getName());
	}

	private void logResult() {

		if ( m_defaultInit ) {
			if ( m_defaultInitMR4C ) {
				m_logger.info("Log4j initialization loaded MR4C logging config");
			} else {
				if ( m_initMR4C ) {
					m_logger.info("Added MR4C logging config from file {} " , m_file);
				} else {
					m_logger.warn("Log4j initialized, but MR4C logging config not found");
				}
			}
		} else {
			if ( m_initMR4C ) {
				m_logger.info("Loaded MR4C logging config only from file {}", m_file);
			} else {
				m_logger.warn("No logging config found, defaulted to BasicConfigurator");
			}
		}
		logAppenders();
	}

	private void logAppenders() {
		Map<String,Set<File>> fileMap = extractAppenderMap();
		for ( String name : fileMap.keySet() ) {
			Set<File> files = fileMap.get(name);
			for ( File file : files ) {
				m_logger.info("Logger {} is going to {}", name, file.getAbsolutePath());
			}
		}
	}

	public Set<File> extractLogFiles() {
		Set<File> result = new HashSet<File>();
		Map<String,Set<File>> fileMap = extractAppenderMap();
		for ( Set<File> files : fileMap.values() ) {
			result.addAll(files);
		}
		return result;
	}

	private Map<String,Set<File>> extractAppenderMap() {
		Map<String,Set<File>> result = new HashMap<String,Set<File>>();
		Enumeration<org.apache.log4j.Logger> e = org.apache.log4j.LogManager.getCurrentLoggers();
		while ( e.hasMoreElements() ) {
			org.apache.log4j.Logger logger = e.nextElement();
			result.put(logger.getName(),extractAppenderFiles(logger));
		}
		return result;
	}

	private Set<File> extractAppenderFiles(org.apache.log4j.Logger logger) {
		Set<File> result = new HashSet<File>();
		Enumeration<Appender> e = logger.getAllAppenders();
		while ( e.hasMoreElements() ) {
			Appender app = e.nextElement();
			if (app instanceof FileAppender ) {
				FileAppender fileApp = (FileAppender) app;
				String filePath = fileApp.getFile();
				if ( filePath==null ) {
					m_logger.warn("Logger {} has file appender {}  with no file", logger.getName(), app.getName());
				} else {
					result.add(new File(filePath));
				}
			}
		}
		return result;
	}

	private void loadLog4jConfigFile(URI uri) throws IOException {
		Properties props = ContentFactories.readContentAsProperties(uri);
		if ( m_defaultInit ) {
			// get rid of root, don't want it twice
			props.remove("log4j.rootLogger");
		}
		PropertyConfigurator.configure(props);
	}

	private void buildLog4jFileFinders() {
		m_log4jFinder = new FileFinder();
		m_log4jFinder.addPropertyStep(CoreConfig.PROP_LOG4J_CONF);
		m_log4jFinder.addLocalFileStep(LOG4J_FILE);
		addInstalledStep(m_log4jFinder, LOG4J_FILE);
		m_log4jFinderNoInstalled = new FileFinder();
		m_log4jFinderNoInstalled.addPropertyStep(CoreConfig.PROP_LOG4J_CONF);
		m_log4jFinderNoInstalled.addLocalFileStep(LOG4J_FILE);
	}

	private void buildLog4cxxFileFinders() {
		m_log4cxxFinder = new FileFinder();
		m_log4cxxFinder.addEnvStep(LOG4CXX_ENV);
		m_log4cxxFinder.addLocalFileStep(LOG4CXX_FILE);
		addInstalledStep(m_log4cxxFinder, LOG4CXX_FILE);
		m_log4cxxFinderNoInstalled = new FileFinder();
		m_log4cxxFinderNoInstalled.addEnvStep(LOG4CXX_ENV);
		m_log4cxxFinderNoInstalled.addLocalFileStep(LOG4CXX_FILE);
	}

	private void addInstalledStep(FileFinder finder, String name) {
		File file = new File(CONF, name);
		finder.addURIStep(file.toURI());
	}

	private void buildFileFilter() {
		m_filter = new FileFinder.FileFilter() {
			public boolean filter(URI uri) throws IOException {
				Properties props = ContentFactories.readContentAsProperties(uri);
				if ( !"true".equals(props.get(PROP_IDENTIFY)) ) {
					return false;
				}
				return true;
			}
		};
	}

	public URI findLog4jConfigFile(boolean includeInstalled) throws IOException {
		return includeInstalled ?
			m_log4jFinder.findFile() :
			m_log4jFinderNoInstalled.findFile();
	}

	public URI findLog4cxxConfigFile(boolean includeInstalled) throws IOException {
		return includeInstalled ?
			m_log4cxxFinder.findFile() :
			m_log4cxxFinderNoInstalled.findFile();
	}

}

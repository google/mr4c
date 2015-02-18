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

package com.google.mr4c.content;

import com.google.mr4c.config.category.MR4CConfig;
import com.google.mr4c.config.category.Category;
import com.google.mr4c.config.category.CoreConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;

import org.apache.hadoop.fs.Path;

public class RelativeContentFactory extends AbstractContentFactory implements ContentFactory {

	private static File s_defaultWD = new File(System.getProperty("user.dir")); // working directory at JVM startup

	public static File getWorkingDirectory() {
		String confWD = MR4CConfig.getDefaultInstance().getCategory(Category.CORE).getProperty(CoreConfig.PROP_ROOT_DIR);
		return StringUtils.isEmpty(confWD) ?
			s_defaultWD :
			new File(confWD);
	}

	public static File toFile(URI uri) {
		return toFile(uri, getWorkingDirectory());
	}

	public static File toFile(URI uri, File parent) {
		if ( !"rel".equals(uri.getScheme()) ) {
			throw new IllegalArgumentException("Expecting a relative file URI [" + uri + "]");
		}
		// Doing this because File won't take a URI with a relative path
		String path = uri.getSchemeSpecificPart();
		return new File(parent, path);
	}

	public static URI toRelativeFileURI(File file) {
		return toRelativeFileURI(file,getWorkingDirectory());
	}
		
	public static URI toRelativeFileURI(File file, File ancestor) {
		String path = toRelativeFilePath(file, ancestor);
		return URI.create("rel:"+path);
	}

	public static String toRelativeFilePath(File file, File ancestor) {
		checkRelated(file, ancestor);
		URI fileUri = file.toURI();
		URI ancestorUri = ancestor.toURI();
		URI relativeUri = ancestorUri.relativize(fileUri);
		return relativeUri.getPath();
	}

	public InputStream readContentAsStream(URI uri) throws IOException {
		File file = toFile(uri);
		return new FileInputStream(file);
	}

	public long getContentLength(URI uri) throws IOException {
		File file = toFile(uri);
		return file.length();
	}

	public OutputStream getOutputStreamForContent(URI uri) throws IOException {
		File file = toFile(uri);
		return new FileOutputStream(file);
	}

	public void ensureParentExists(URI uri) throws IOException {
		File file = toFile(uri);
		LocalContentFactory.ensureParentExists(file);
	}

	public boolean exists(URI uri) throws IOException {
		File file = toFile(uri);
		return file.exists();
	}

	public boolean deleteContent(URI uri) throws IOException {
		File file = toFile(uri);
		return file.delete();
	}

	public Path toPath(URI uri) {
		File file = toFile(uri);
		return new Path(file.toURI());
	}

	private static void checkRelated(File file, File ancestor) {
		if (!file.getAbsolutePath().startsWith(ancestor.getAbsolutePath())) {
			throw new IllegalStateException(String.format("[%s] is not a decendent of [%s]", file, ancestor));
		}
	}

}



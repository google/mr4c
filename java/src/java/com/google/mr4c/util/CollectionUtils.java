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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

public class CollectionUtils {

	/**
	  * Convert the list of lists into a single list
	*/
	public static <T> List<T> concatenate(List<List<T>> lists) {
		List<T> result = new ArrayList<T>();
		for ( List<T> list : lists ) {
			result.addAll(list);
		}
		return result;
	}

	/**
	  * Partition a list into <code>num</code> sub-lists.  If the list does
	  * not divide evenly, the extra 'n' elements are split across the
	  * first 'n' lists.  There will be no more lists than elements returned (i.e. no empty lists tacked on to the end)
	*/
	public static <T> List<List<T>> partition(List<T> list, int num) {
		if ( num<1 ) {
			throw new IllegalArgumentException("Number of sub-lists must be greater than zero");
		}
		List<List<T>> result = new ArrayList<List<T>>();
		int index=0;
		int listsRemaining = num;
		int elementsRemaining = list.size();
		while ( elementsRemaining>0 ) {
			int size = (int) Math.ceil(elementsRemaining / (listsRemaining+0.0));
			List<T> subList = list.subList(index, index+size);
			result.add(subList);
			listsRemaining--;
			elementsRemaining-=size;
			index+=size;
		}
		if ( elementsRemaining!=0 ) {
			throw new IllegalStateException(String.format("Loop exited with %d elements still remaining", elementsRemaining));
		}
		return result;
	}

	/**
	  * Partition a list into sub-lists with <code>size</code> elements
	  * each.  If the list does not divide evenly, the extra elements are
	  * all in the final list.
	*/
	public static <T> List<List<T>> partitionBySize(List<T> list, int size) {
		if ( size<1 ) {
			throw new IllegalArgumentException("Size of sub-lists must be greater than zero");
		}
		List<List<T>> result = new ArrayList<List<T>>();
		int index=0;
		int elementsRemaining = list.size();
		while ( elementsRemaining>0 ) {
			int listSize = Math.min(elementsRemaining, size);
			List<T> subList = list.subList(index, index+listSize);
			result.add(subList);
			elementsRemaining-=listSize;
			index+=listSize;
		}
		if ( elementsRemaining!=0 ) {
			throw new IllegalStateException(String.format("Loop exited with %d elements still remaining", elementsRemaining));
		}
		return result;
	}

	/**
	  * Introduces overlap into a series of lists. 
	  * @param before # of elements from the end of the previous list to prepend
	  * @param after # of elements from the beginning of the next list to append
	*/
	public static <T> List<List<T>> overlap(List<List<T>> lists, int before, int after) {

		if ( before <0 ) {
			throw new IllegalArgumentException("Value of before cannot be negative");
		}
		if ( after <0 ) {
			throw new IllegalArgumentException("Value of after cannot be negative");
		}

		ListIterator<List<T>> iter = lists.listIterator();

		List<List<T>> result = new ArrayList<List<T>>();
		for ( ; iter.hasNext(); ) {
			List<T> current = new ArrayList<T>(iter.next());
			List<T> prev = before>0 ?  findPrevious(iter) : null;
			List<T> next = after>0 ?  findNext(iter) : null;
			if ( prev!=null ) {
				List<T> overlap = prev.subList(prev.size()-before, prev.size());
				current.addAll(0, overlap);
			}
			if ( next!=null ) {
				List<T> overlap = next.subList(0, after);
				current.addAll(overlap);
			}
			result.add(current);
		}

		return result;
	}


	private static <T> T findPrevious(ListIterator<T> iter) {
		T prev = null;
		iter.previous(); // rewind
		if ( iter.hasPrevious() ) {
			prev = iter.previous();
			iter.next(); // come back
		}
		iter.next(); // come back
		return prev;
	}

	private static <T> T findNext(ListIterator<T> iter) {
		T next = null;
		if ( iter.hasNext() ) {
			next = iter.next();
			iter.previous(); // come back
		}
		return next;
	}

	public static Properties toProperties(Iterable<Map.Entry<String,String>> propSrc) {
		Properties props = new Properties();
		for ( Map.Entry<String,String> entry : propSrc ) {
			String name = entry.getKey();
			String val = entry.getValue();
			props.setProperty(name,val);
		}
		return props;
	}

	public static Map<String,String> toMap(Properties props) {
		return new HashMap(props);
	}

	// only captures adds and mods, not deletes
	public static Properties extractChanges(Properties start, Properties end) {
		Properties diff = new Properties();
		for ( String name : end.stringPropertyNames() ) {
			if ( start.getProperty(name)==null || !start.getProperty(name).equals(end.getProperty(name)) ) {
				diff.setProperty(name, end.getProperty(name));
			} 
		}
		return diff;
	}

	/**
	  * Returns new properties with <code>String.trim()</code> applied to every property value.  The original properties are unchanged.
	*/
	public static Properties toTrimmedProperties(Properties props) {
		Properties trimmed = new Properties();
		for ( String name : props.stringPropertyNames() ) {
			String val = props.getProperty(name);
			val = val==null ? val : val.trim();
			trimmed.setProperty(name, val);
		}
		return trimmed;
	}

	/**
	  * Converts properties to file storage format
	*/
	public static String toFileContent(Properties props) {
		try {
			StringWriter writer = new StringWriter();
			props.store(writer,"");
			return writer.toString();
		} catch (IOException ioe) {
			throw new IllegalStateException(ioe); // shouldn't happen
		}
	}

	/**
	  * Converts properties from file storage format
	*/
	public static Properties fromFileContent(String content) {
		try {
			StringReader reader = new StringReader(content);
			Properties props = new Properties();
			props.load(reader);
			return props;
		} catch (IOException ioe) {
			throw new IllegalStateException(ioe); // shouldn't happen
		}
	}

	public static void clearProperties(Properties props, Collection<String> names) {
		props.keySet().removeAll(names);
	}

}

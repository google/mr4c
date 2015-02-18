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

#ifndef __MR4C_ARRAY_UTIL_H__
#define __MR4C_ARRAY_UTIL_H__

#include <set>
#include <map>
#include <vector>
#include <sstream>


namespace MR4C {

/**
  * Copy an array into space allocated by the caller
*/ 
template<typename T> void copyArray(const T* src, T* dest, size_t size) {
	std::copy(src, src+size, dest);
}

/**
  * Copy an array into space allocated by this method.
  * The array is allocated with new[].
  * Caller is responsible for disposing with delete[].
*/
template<typename T> T* copyArray(const T* src, size_t size) {
	T* dest = new T[size];
	copyArray<T>(src,dest,size);
	return dest;
}

/**
  * Memberwise array comparison of arrays known to be the same size.
*/
template<typename T> bool compareArray(const T* arr1, const T* arr2, size_t size) {
	return std::equal(arr1, arr1+size, arr2);
}


/** 
  * Helper method for compareVectorOfPointers
*/
template<typename T>
bool comparePointers(const T* p1, const T* p2 ) {
	return *p1==*p2;
}

/**
  * Operator== for vectors where the elements are pointers.
  * The elements comparison will compare the dereferenced pointers 
*/
template<typename T>
bool compareVectorsOfPointers(
	const std::vector<T*> vector1,
	const std::vector<T*> vector2
) {
	if ( vector1.size()!=vector2.size() ) return false;
	return std::equal(vector1.begin(), vector1.end(), vector2.begin(), &comparePointers<T>);
}

/**
  * Deletes all the values in a vector of pointers
*/
template<typename V>
void deleteVectorOfPointers( std::vector<V*>& vector) {
	for ( typename std::vector<V*>::iterator iter = vector.begin(); iter!=vector.end(); iter++ ) {
		delete *iter;
	}
}

/** 
  * Helper method for compareMapOfPointers
*/
template<typename K, typename V>
bool comparePointerPairs(
	const std::pair<K,V*>& pair1,
	const std::pair<K,V*>& pair2
) {

	// check keys
	if ( pair1.first!=pair2.first ) {
		return false;
	}
	// check values
	return *pair1.second==*pair2.second;
}

/**
  * Operator== for maps where the elements are pointers.
  * The elements comparison will compare the dereferenced pointers 
*/
template<typename K, typename V>
bool compareMapsOfPointers(
	const std::map<K,V*> map1,
	const std::map<K,V*> map2
) {
	if ( map1.size()!=map2.size() ) return false;
	return std::equal(map1.begin(), map1.end(), map2.begin(), &comparePointerPairs<K,V>);
}

/**
  * Deletes all the values in a map of pointers
*/
template<typename K, typename V>
void deleteMapOfPointers( std::map<K,V*>& map) {
	for ( typename std::map<K,V*>::iterator iter = map.begin(); iter!=map.end(); iter++ ) {
		delete iter->second;
	}
}


/**
  * Returns all the keys in a map.
  * C++ equivalent of java.util.Map.keySet()
*/
template<typename K, typename V>
std::set<K> keySet(const std::map<K,V>& map) {
	std::set<K> result;
	typename std::map<K,V>::const_iterator iter=map.begin();
	for ( ; iter!=map.end(); iter++ ) {
		result.insert(iter->first);
	}
	return result;
}

/**
  * Add contents of map2 to map1, updating map1.  If a key already exists in map1, it is updated to the value from map2
*/
template<typename K, typename V>
void mapAddAll(std::map<K,V>* map1, const std::map<K,V>& map2) {
	typename std::map<K,V>::const_iterator iter=map2.begin();
	for ( ; iter!=map2.end(); iter++ ) {
		(*map1)[iter->first] = iter->second; // making sure the original values are overwritten
	}
}

/**
  * Converts elements between begin and end to a delimited string.  The elements must implement operator<<
*/
template<typename InputIterator>
std::string containerToString(const InputIterator begin, const InputIterator end, const std::string& delim) {
	if ( begin==end ) {
		return "";
	}
	std::ostringstream ss;
	InputIterator iter = begin;
	InputIterator ahead = begin;
	ahead++;
	for ( ; iter!=end; iter++ ) {
		ss << *iter;
		if ( ahead!=end ) {
			ss << delim << " ";
			ahead++;
		}
	}
	return ss.str();
}

/**
  * Converts a vector to a delimited string.  The elements must implement operator<<
*/
template<typename T>
std::string vectorToString(const std::vector<T>& vec, const std::string& delim) {
	return containerToString(vec.begin(), vec.end(), delim);
}

/**
  * Converts a set to a delimited string.  The elements must implement operator<<
*/
template<typename T>
std::string setToString(const std::set<T>& set, const std::string& delim) {
	return containerToString(set.begin(), set.end(), delim);
}

/**
  * Converts a set to a vector using the natural set ordering
*/
template<typename T>
std::vector<T> setToVector(const std::set<T>& set) {
	return std::vector<T>(set.begin(), set.end());
}

}

#endif

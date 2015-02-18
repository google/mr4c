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

package com.google.mr4c.metadata;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PrimitiveFactory  {

	private static Map<PrimitiveType,PrimitiveFormat> s_formats = new HashMap<PrimitiveType,PrimitiveFormat>();

	public static MetadataField parseField(String str, PrimitiveType type) {
		PrimitiveFormat format = s_formats.get(type);
		Object val = format.parse(str);
		return new MetadataField(val,type);
	}

	public static MetadataArray parseArray(String[] strs, PrimitiveType type) {
		return parseArray(Arrays.asList(strs), type);
	}

	public static MetadataArray parseArray(List<String> strs, PrimitiveType type) {
		PrimitiveFormat format = s_formats.get(type);
		List vals = new ArrayList();
		for ( String str : strs ) {
			vals.add(format.parse(str));
		}
		return new MetadataArray(vals,type);
	}

	private static interface PrimitiveFormat<T> {

		T parse(String str);

	}

	private static class BooleanFormat implements PrimitiveFormat<Boolean> {
	
		public Boolean parse(String str) {
			return Boolean.parseBoolean(str);
		}
		

	}
	static { s_formats.put(PrimitiveType.BOOLEAN, new BooleanFormat()); }

	private static class ByteFormat implements PrimitiveFormat<Byte> {
	
		public Byte parse(String str) {
			return Byte.parseByte(str);
		}
		
	}
	static { s_formats.put(PrimitiveType.BYTE, new ByteFormat()); }

	private static class IntegerFormat implements PrimitiveFormat<Integer> {
	
		public Integer parse(String str) {
			return Integer.parseInt(str);
		}
		
	}
	static { s_formats.put(PrimitiveType.INTEGER, new IntegerFormat()); }

	private static class FloatFormat implements PrimitiveFormat<Float> {
	
		public Float parse(String str) {
			return Float.parseFloat(str);
		}
		

	}
	static { s_formats.put(PrimitiveType.FLOAT, new FloatFormat()); }

	private static class DoubleFormat implements PrimitiveFormat<Double> {
	
		public Double parse(String str) {
			return Double.parseDouble(str);
		}
		

	}
	static { s_formats.put(PrimitiveType.DOUBLE, new DoubleFormat()); }

	private static class StringFormat implements PrimitiveFormat<String> {
	
		public String parse(String str) {
			return str;
		}
		
	}
	static { s_formats.put(PrimitiveType.STRING, new StringFormat()); }

	private static class LongFormat implements PrimitiveFormat<Long> {
	
		public Long parse(String str) {
			return Long.parseLong(str);
		}
		
	}
	static { s_formats.put(PrimitiveType.SIZE_T, new LongFormat()); }

	private static class LongDoubleFormat implements PrimitiveFormat<BigDecimal> {
	
		public BigDecimal parse(String str) {
			return new BigDecimal(str, MathContext.DECIMAL128);
		}
		

	}
	static { s_formats.put(PrimitiveType.LONG_DOUBLE, new LongDoubleFormat()); }

}

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

package com.google.mr4c.config.category;

public enum Category implements CategoryInfo {

	CORE(
		"core",
		"-C",
		"MR4C_CORE"
	),

	ALGO(
		"algo",
		"-A",
		"MR4C_ALGO_PARAMS"
	),

	RUNTIME(
		"runtime",
		"-R",
		"MR4C_RUNTIME"
	),

	TOPICS(
		"topics",
		"-T",
		"MR4C_TOPICS"
	),

	HADOOP(
		"hadoop",
		"-H",
		"MR4C_HADOOP"
	),

	STATS(
		"stats",
		"-Z",
		"MR4C_STATS"
	),

	CUSTOM(
		"custom",
		null,
		null
	),

	RAW(
		"raw",
		null,
		null
	),

	S3(
		"s3",
		"-S3",
		"MR4C_S3"
	);

	private String m_name;
	private String m_argPrefix;
	private String m_envVar;
	private String m_categoryProp;
	private String m_propPrefix;

	private static Category s_default = RUNTIME;

	private Category(
		String name,
		String argPrefix,
		String envVar
	) {
		m_name = name;
		m_argPrefix = argPrefix;
		m_envVar = envVar;
		m_categoryProp = "mr4c." + name;
		m_propPrefix = m_categoryProp + ".";
	}

	public String getCategoryName() {
		return m_name;
	}

	public String getCategoryProperty() {
		return m_categoryProp;
	}

	public String getArgsPrefix() {
		return m_argPrefix;
	}

	public String getPropertiesPrefix() {
		return m_propPrefix;
	}

	public String getEnvironmentVariable() {
		return m_envVar;
	}

	public boolean isDefaultCategory() {
		return this==s_default;
	}

	public static Category getDefaultCategory() {
		return s_default;
	}

}

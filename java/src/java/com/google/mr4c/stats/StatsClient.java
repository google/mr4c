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

package com.google.mr4c.stats;

public interface StatsClient {

	boolean timing(String key, int value);

	boolean timing(String key, int value, double sampleRate);

	boolean decrement(String key);

	boolean decrement(String key, int magnitude);

	boolean decrement(String key, int magnitude, double sampleRate);

	boolean decrement(String... keys);

	boolean decrement(int magnitude, String... keys);

	boolean decrement(int magnitude, double sampleRate, String... keys);

	boolean increment(String key);

	boolean increment(String key, int magnitude);

	boolean increment(String key, int magnitude, double sampleRate);

	boolean increment(int magnitude, double sampleRate, String... keys);

	boolean gauge(String key, double magnitude);

	boolean gauge(String key, double magnitude, double sampleRate);

        boolean flush();

}

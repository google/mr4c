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

package com.google.mr4c.message;

import com.google.mr4c.util.MR4CLogging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import org.slf4j.Logger;

public class HttpMessageHandler implements MessageHandler {

	protected static final Logger s_log = MR4CLogging.getLogger(HttpMessageHandler.class);

	private URI m_uri;
	private HttpClient m_client;

	public HttpMessageHandler() {
		m_client = new DefaultHttpClient();
		m_client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	}

	public void setURI(URI uri) {
		m_uri = uri;
	}
		
	public void handleMessage(Message msg) throws IOException {
		
		HttpPost post = new HttpPost(m_uri);
		post.setEntity(
			new StringEntity(
				msg.getContent(),
				ContentType.create(msg.getContentType())
			)
		);

		s_log.info("POSTing message to [{}]: [{}]", m_uri, msg);
		HttpResponse response = m_client.execute(post);
		StatusLine statusLine = response.getStatusLine();
		s_log.info("Status line: {}", statusLine);
		s_log.info("Content: {}", toString(response.getEntity()));
		if ( statusLine.getStatusCode()>=300 ) {
			throw new IOException(statusLine.toString());
		}

	}

	private String toString(HttpEntity entity) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		entity.writeTo(out);
		return out.toString();
	}
}

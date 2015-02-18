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

#ifndef __MR4C_MESSAGE_H__
#define __MR4C_MESSAGE_H__

#include <string>

namespace MR4C {

class MessageImpl;

class Message {

	public:

		Message();

		Message(
			const std::string& topic, 
			const std::string& content,
			const std::string& contentType = "text/plain"
		);

		Message(const Message& msg);

		std::string getTopic() const;
		
		std::string getContent() const;
		
		std::string getContentType() const;
	
		std::string str() const;
	
		~Message();

		Message& operator=(const Message& msg);

		bool operator==(const Message& msg) const;
		bool operator!=(const Message& msg) const;

	private:
		MessageImpl* m_impl;

};

std::ostream& operator<<(std::ostream& os, const Message& msg);


}
#endif


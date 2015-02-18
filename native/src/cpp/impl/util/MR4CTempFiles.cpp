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

#define _XOPEN_SOURCE 500 // Required for nftw()
#include <ftw.h>
#include <map>
#include <cerrno>
#include <cstdlib>
#include <cstring>
#include <ctime>
#include <iostream>
#include <stdexcept>
#include <mutex>
#include <log4cxx/logger.h>

#include "util/util_api.h"

using log4cxx::LoggerPtr;

namespace MR4C {


class MR4CTempFilesImpl {


	friend class MR4CTempFiles;

	private:

		std::set<std::string> m_dirs;
		std::mutex m_mutex;

		// This object is a singleton, this is static only so we can access it from the static
		// callback function in the directory tree walk
		//static LoggerPtr m_logger;
		
		// Static logger causes log4cxx to blow up on process exit
		// Suggested approach is to have a getLogger() method, then directoryWalk calls MR4CTempFiles::instance().getLogger()
		// Its roundabout, but keeps things from blowing up

		LoggerPtr m_logger;

		MR4CTempFilesImpl() {
		    m_logger = MR4CLogging::getLogger("util.MR4CTempFiles");
		}

		std::set<std::string> getAllocatedDirectories() {
			return m_dirs;
		}

		std::string createTempDirectory(const std::string& parent) {
			std::string path = createDirectoryTemplate(parent);
			char temp[200]; // need this to write into
			strcpy(temp, path.c_str()); // Can't use string::copy method, it doesn't null terminate
			std::unique_lock<std::mutex> lock(m_mutex); // Sync directory creation and set access
                if ( mkdtemp(temp)==NULL ) {
                    MR4C_THROW(std::runtime_error, "Failed to create temp directory [" << path << "]; errno is " << errno);
                }
                path = temp;
                m_dirs.insert(path);
			lock.unlock();
			return path;
		}

		std::string createDirectoryTemplate(const std::string& parent) {
			time_t ts;
			MR4C_RETURN_STRING(parent << "/mr4ctemp" << time(&ts) << "_XXXXXX");
		}

		void deleteAllocatedDirectories() {
            for (std::string dir : m_dirs) {
                int flags = FTW_DEPTH; // Depth-first: traverse files in directory before the directory
                int nopenfds = 20; // Limit to 20 open file descriptors

                if (nftw(dir.c_str(), directoryWalk, nopenfds, FTW_DEPTH) != 0) {
                    LOG4CXX_ERROR(m_logger, "Failed to delete temporary directory: " << dir);
                }
            }
		}

		static int directoryWalk(const char *path, const struct stat *sb, int flag, struct FTW *ftwbuf) {
		    // Remove remove files and directories, otherwise report error and continue
		    if (flag == FTW_F ||
		        flag == FTW_D ||
		        flag == FTW_DP) {
		        // TODO restore // LOG4CXX_INFO(m_logger, "Removed temporary " << ((flag == FTW_F) ? "file: " : "directory: ") << path);
		        remove(path);
		    } else {
		        // TODO restore // LOG4CXX_INFO(m_logger, "Unknown temporary file reference: " << path);
		    }

		    return 0; // Tell nftw to continue
		}

};

// Required so the file will compile with static member variable
//LoggerPtr MR4CTempFilesImpl::m_logger;

MR4CTempFiles& MR4CTempFiles::instance() {
	static MR4CTempFiles s_instance;
	return s_instance;
}

MR4CTempFiles::MR4CTempFiles() {
	m_impl = new MR4CTempFilesImpl();
}

MR4CTempFiles::~MR4CTempFiles() {
	delete m_impl;
} 

std::string MR4CTempFiles::createTempDirectory(const std::string& parent) {
	return m_impl->createTempDirectory(parent);
}

std::set<std::string> MR4CTempFiles::getAllocatedDirectories() {
	return m_impl->getAllocatedDirectories();
}

void MR4CTempFiles::deleteAllocatedDirectories() {
    m_impl->deleteAllocatedDirectories();
}


}

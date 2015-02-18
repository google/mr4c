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

#ifndef __MR4C_SIMULTANEOUSTHREADRUNNER_H__
#define __MR4C_SIMULTANEOUSTHREADRUNNER_H__

#include <thread>
#include <mutex>
#include <condition_variable>

/**
 * Helper class for running a function in multiple threads simultaneously.
 *
 * Override this class and implement the run() method to run code in
 * a configurable number of threads simultaneously. Creates the threads
 * before running the code, and then uses locks to try to start all of
 * the threads executing the user code as close to simultaneously as possible.
 */
class SimultaneousThreadRunner {

    public:
        SimultaneousThreadRunner( int numThreads = 10 );
        virtual ~SimultaneousThreadRunner();

        /**
         * Start the threads executing the code as close to simultaneously as possible.
         */
        void start();

        /**
         * Wait for all threads to finish execution. Don't forget to call this before
         * deleting the thread runner or letting it fall out of scope, otherwise the
         * threads might be destroyed while still executing, causing hard-to-debug
         * errors.
         */
        void join();

    protected:
        /**
         * Override this method to define the code that gets run in threads.
         */
        virtual void run() = 0;

    private:
        std::thread * m_pool;
        std::mutex m_mutex;
        std::condition_variable m_cv;
        int m_numThreads;
        bool m_ready;

        /**
         * Static entry point for the threads themselves. Used to run the code
         * in the implemented run() method.
         */
        static void threadFunc( SimultaneousThreadRunner * runner );

};

#endif

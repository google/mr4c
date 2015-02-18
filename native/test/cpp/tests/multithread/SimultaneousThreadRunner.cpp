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

#include "multithread/SimultaneousThreadRunner.h"

SimultaneousThreadRunner::SimultaneousThreadRunner(int numThreads) {
    m_ready = false;
    m_numThreads = numThreads;
    m_pool = new std::thread[numThreads];
}

SimultaneousThreadRunner::~SimultaneousThreadRunner() {
    delete[] m_pool;
}

void SimultaneousThreadRunner::start()
{
    for ( int i = 0; i < m_numThreads; ++i ) {
        m_pool[i] = std::thread( SimultaneousThreadRunner::threadFunc, this );
    }
    std::unique_lock<std::mutex> lock(m_mutex);
    m_ready = true;
    m_cv.notify_all();
}

void SimultaneousThreadRunner::join() {
    for ( int i = 0; i < m_numThreads; ++i ) {
        m_pool[i].join();
    }
}

void SimultaneousThreadRunner::threadFunc( SimultaneousThreadRunner * runner ) {
    std::unique_lock<std::mutex> lock(runner->m_mutex);
    while (!runner->m_ready)
        runner->m_cv.wait(lock);
    lock.unlock();
    runner->run();
}

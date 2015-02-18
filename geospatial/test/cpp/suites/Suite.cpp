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

#include <iostream>

#include <cppunit/extensions/TestFactoryRegistry.h>
#include <cppunit/TestResult.h>
#include <cppunit/TestResultCollector.h>
#include <cppunit/TestRunner.h>
#include <cppunit/BriefTestProgressListener.h>
#include <cppunit/XmlOutputter.h>
#include "MR4CGeoTests.h"

int main( int argc, char* argv[])
{

	std::string name = MR4C::extractTestName(argc, argv);

	// informs test-listener about test-results
	CPPUNIT_NS::TestResult testResult;

	// register listener for collecting the test-results
	CPPUNIT_NS::TestResultCollector collectedResults;
	testResult.addListener( &collectedResults);

	// register listener for per-test progress output
	CPPUNIT_NS::BriefTestProgressListener progress;
	testResult.addListener( &progress);

	// insert test-suite at test-runner by registry
	CPPUNIT_NS::TestRunner testRunner;
	testRunner.addTest( CPPUNIT_NS::TestFactoryRegistry::getRegistry(name).makeTest());
	testRunner.run(testResult);

	// important stuff happens next
	std::ofstream xmlFileOut("reports/testResults.xml");
	CPPUNIT_NS::XmlOutputter xmlOut( &collectedResults, xmlFileOut);
	xmlOut.write();

	// return 0 if tests were successful
	return collectedResults.wasSuccessful() ? 0 : 1;
}

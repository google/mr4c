#include <iostream>

#include <cppunit/extensions/TestFactoryRegistry.h>
#include <cppunit/TestResult.h>
#include <cppunit/TestResultCollector.h>
#include <cppunit/TestRunner.h>
#include <cppunit/BriefTestProgressListener.h>
#include <cppunit/XmlOutputter.h>

int main( int argc, char* argv[])
{

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
	testRunner.addTest( CPPUNIT_NS::TestFactoryRegistry::getRegistry().makeTest());
	testRunner.run(testResult);

	// important stuff happens next
	std::ofstream xmlFileOut("reports/testResults.xml");
	CPPUNIT_NS::XmlOutputter xmlOut( &collectedResults, xmlFileOut);
	xmlOut.write();

	// return 0 if tests were successful
	return collectedResults.wasSuccessful() ? 0 : 1;
}



#include <cppunit/extensions/TestFactoryRegistry.h>
#include <cppunit/TextTestRunner.h>

// local test runner that just dumps to the console
int main( int argc, char* argv[])
{
	CPPUNIT_NS::TextTestRunner testrunner;
	testrunner.addTest( CPPUNIT_NS::TestFactoryRegistry::getRegistry().makeTest());
	testrunner.run();

}



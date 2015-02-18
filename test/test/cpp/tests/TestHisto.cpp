#include <stdexcept>
#include <cstring>
#include <stdlib.h>
#include <sys/types.h>
#include <dirent.h>
#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>

#define TEST_DIR "test/cpp/tests/"
#define OPENJPEG_LIB_DIR "../../../openjpeg/libopenjpeg/.libs"
//#define OPENJPEG_LIB_DIR "/usr/local/openjpeg-1.4-r697/lib"

class TestHisto : public CPPUNIT_NS::TestFixture {

	CPPUNIT_TEST_SUITE(TestHisto);
	CPPUNIT_TEST(testOutputFilesExist);
    CPPUNIT_TEST(test4Output);
    CPPUNIT_TEST(testQuartileOutput);
    CPPUNIT_TEST(testBackgroundOutput);
	CPPUNIT_TEST_SUITE_END();


public:

    void setUp() {

    }
    
    
    
    void tearDown() {
    }
    
    void testOutputFilesExist() {

        system( "cd " TEST_DIR " ; LD_LIBRARY_PATH=$LD_LIBRARY_PATH:"
                OPENJPEG_LIB_DIR":../../../dist mr4c rel:algo_config.json" );
        system( "cd " TEST_DIR " ; LD_LIBRARY_PATH=$LD_LIBRARY_PATH:"
                OPENJPEG_LIB_DIR":../../../dist mr4c rel:reduce_algo_config.json" );

        const int NUM_FILES = 5;
        const char * fnames[ NUM_FILES ] = { "4.csv","912-white-720-others.csv",
                                             "8-0-4-50-6-75.csv", "back-75-14-white.csv", 
                                             "avg_pixels.csv" };

        for ( int i = 0; i < NUM_FILES; ++i ) {
            DIR * output_dir = opendir( TEST_DIR "output" );
            CPPUNIT_ASSERT( output_dir );
            bool found = false;
            while ( struct dirent * d = readdir( output_dir )) {
                if ( strcmp( fnames[ i ], d->d_name ) == 0 ) {
                    found = true;
                    break;
                }
            }                
            CPPUNIT_ASSERT( found );
        }
    }
	
    void test4Output() {
        const char * output4="4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3068";
        FILE * f = fopen( TEST_DIR "output/4.csv", "r" );
        CPPUNIT_ASSERT_MESSAGE( "output/4.csv doesn't exist", f!=NULL );
        char input_line[ 512 ];
        fgets( input_line, 512, f );
        CPPUNIT_ASSERT( strcmp( output4, input_line ) == 0 );
    }

    void test80Output() {
        const char * output4="8,0,0,0,0,0,0,0,0,4,0,0,0,0,0,6,0,0,0,3054";
        FILE * f = fopen( TEST_DIR "output/8-0-4-50-6-75.csv", "r" );
        CPPUNIT_ASSERT_MESSAGE( "output/8-0-4-50-6-75.csv doesn't exist", f!=NULL );
        char input_line[ 512 ];
        fgets( input_line, 512, f );
        CPPUNIT_ASSERT( strcmp( output4, input_line ) == 0 );
    }

    void testQuartileOutput() {
        const char * output4="720,0,0,0,0,0,0,0,0,720,0,0,0,0,720,0,0,0,0,912";
        FILE * f = fopen( TEST_DIR "output/912-white-720-others.csv", "r" );
        CPPUNIT_ASSERT_MESSAGE( "output/912-white-720-others.csv doesn't exist", f!=NULL );
        char input_line[ 512 ];
        fgets( input_line, 512, f );
        CPPUNIT_ASSERT( strcmp( output4, input_line ) == 0 );
    }
    void testBackgroundOutput() {
        const char * output4="0,0,0,0,0,0,0,0,0,0,0,0,0,0,3058,0,0,0,0,14";
        FILE * f = fopen( TEST_DIR "output/back-75-14-white.csv", "r" );
        CPPUNIT_ASSERT_MESSAGE( "output/back-75-14-white.csv doesn't exist", f!=NULL );
        char input_line[ 512 ];
        fgets( input_line, 512, f );
        CPPUNIT_ASSERT( strcmp( output4, input_line ) == 0 );
    }
	
    
};

CPPUNIT_TEST_SUITE_REGISTRATION(TestHisto);



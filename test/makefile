SRC =./src/cpp
IMPL=$(SRC)
TEST =./test/cpp/tests
SUITE =./test/cpp/suites
BIN = ./bin
DIST = ./dist
EXE = ./exe
REPORTS = ./reports
DOCS	= ./docs
OBJ_DIR = ./objs

MR4C_HOME ?= /usr/local/mr4c
MR4C_INCL_DIR = $(MR4C_HOME)/native/include
OPENJPEG_LIB_DIR = ./openjpeg/libopenjpeg/.libs
OPENJPEG_INCL_DIR = ./openjpeg/libopenjpeg
IMPL_OBJ_DIR = $(OBJ_DIR)/impl
TEST_OBJ_DIR = $(OBJ_DIR)/test
SUITE_OBJ_DIR = $(OBJ_DIR)/suite

IMPL_SRCS = $(shell find $(IMPL) -name *.cpp)
IMPL_OBJS = $(patsubst $(IMPL)%.cpp, $(IMPL_OBJ_DIR)%.o, $(IMPL_SRCS) )

TEST_SRCS = $(shell find $(TEST) -name *.cpp)
TEST_OBJS = $(patsubst $(TEST)%.cpp, $(TEST_OBJ_DIR)%.o, $(TEST_SRCS) )

IMPL_SRC_DIRS = $(shell find $(IMPL) -type d \( ! -path '*/.*' \) )
IMPL_OBJ_DIRS = $(patsubst $(IMPL)%, $(IMPL_OBJ_DIR)%, $(IMPL_SRC_DIRS) )

TEST_SRC_DIRS = $(shell find $(TEST) -type d \( ! -path '*/.*' \) )
TEST_OBJ_DIRS = $(patsubst $(TEST)%, $(TEST_OBJ_DIR)%, $(TEST_SRC_DIRS) )

#default target
all: clean lib test 

################
# IMPL SRC RULES
################

$(IMPL_OBJ_DIR)/%.o: $(IMPL)/%.cpp	
	./openjpeg_build.sh
	g++ -std=c++0x -I$(MR4C_INCL_DIR) -I$(OPENJPEG_INCL_DIR) -fPIC -Wall -g -o $@ -c $< 

################
# TEST SRC RULES
################

$(TEST_OBJ_DIR)/%.o: $(TEST)/%.cpp
	g++ -std=c++0x  -fPIC -Wall -o $@ -c $< 

$(SUITE_OBJ_DIR)/%.o: $(SUITE)/%.cpp
	g++ -std=c++0x  -fPIC -Wall -o $@ -c $<

LIB = $(DIST)/libhistogram.so
LOCAL_EXE = $(EXE)/test_local
TEST_EXE = $(EXE)/run_tests
DOCS_EXE = /usr/bin/doxygen
DOCS_CONFIG = ./conf/Doxyfile

#HEADER_OPTS = -I$(API) -I$(IMPL)


$(LIB): $(IMPL_OBJS)	
	g++ -std=c++0x -Wall  -L$(MR4C_HOME)/native/lib -L$(OPENJPEG_LIB_DIR)  -o $@ $(IMPL_OBJS) -g -lmr4c -lopenjpeg -shared	

$(LOCAL_EXE): $(IMPL_OBJS) $(TEST_OBJS) $(SUITE_OBJ_DIR)/Local.o
	g++ -Wall -std=c++0x  -rdynamic -o $@  $(TEST_OBJS)  $(SUITE_OBJ_DIR)/Local.o -lcppunit -ldl

$(TEST_EXE): $(IMPL_OBJS) $(TEST_OBJS) $(SUITE_OBJ_DIR)/Suite.o
	g++ -Wall -std=c++0x  -rdynamic -o $@ $(TEST_OBJS) $(SUITE_OBJ_DIR)/Suite.o -lcppunit -ldl


clean:
	rm -rf ./openjpeg
	rm -rf $(OBJ_DIR)
	rm -rf $(DIST)
	rm -rf $(EXE)
	rm -rf $(REPORTS)
	rm -rf $(DOCS)
	rm -rf $(TEST)/output

compile: init $(IMPL_OBJS)


test_compile: init compile example_compile $(TEST_OBJS)

lib: init $(LIB)

test: init $(TEST_EXE) $(LOCAL_EXE)


docs: init 
	$(DOCS_EXE) $(DOCS_CONFIG)

# How to override a parameter in the doxygen config file
# http://www.stack.nl/~dimitri/doxygen/faq.html
local_docs: init
	( cat $(DOCS_CONFIG); echo "OUTPUT_DIRECTORY=$(DOCS)" ) | $(DOCS_EXE) -

init:
	mkdir -p $(IMPL_OBJ_DIRS)
	mkdir -p $(TEST_OBJ_DIRS)
	mkdir -p $(SUITE_OBJ_DIR)
	mkdir -p $(DIST)
	mkdir -p $(EXE)
	mkdir -p $(REPORTS)
	mkdir -p $(DOCS)

.PHONY: clean, lib, test, init, all, compile, test_compile


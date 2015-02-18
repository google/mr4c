# Provide overrides of ?= variables in makefile.local
-include makefile.local

SRC := ./src/cpp
API := $(SRC)/api
IMPL := $(SRC)/impl
TEST_API := ./test/cpp/api
TEST := ./test/cpp/tests
SUITE := ./test/cpp/suites
BIN := ./bin
CONF := ./conf
TOOLS :=./tools
DIST ?= ./dist
EXE ?= ./exe
REPORTS ?= ./reports
DOCS ?= ./docs
OBJ_DIR ?= ./objs
OUTPUT_DIR ?= ./output

IMPL_OBJ_DIR := $(OBJ_DIR)/impl
TEST_OBJ_DIR := $(OBJ_DIR)/test
SUITE_OBJ_DIR := $(OBJ_DIR)/suite
COVER_OBJ_DIR := $(OBJ_DIR)/cover

IMPL_SRCS := $(shell find $(IMPL) -name *.cpp)
IMPL_OBJS := $(patsubst $(IMPL)%.cpp, $(IMPL_OBJ_DIR)%.o, $(IMPL_SRCS) )
COVER_OBJS := $(patsubst $(IMPL)%.cpp, $(COVER_OBJ_DIR)%.o, $(IMPL_SRCS) )

TEST_SRCS := $(shell find $(TEST) -name *.cpp)
TEST_OBJS := $(patsubst $(TEST)%.cpp, $(TEST_OBJ_DIR)%.o, $(TEST_SRCS) )

IMPL_SRC_DIRS := $(shell find $(IMPL) -type d \( ! -path '*/.*' \) )
IMPL_OBJ_DIRS := $(patsubst $(IMPL)%, $(IMPL_OBJ_DIR)%, $(IMPL_SRC_DIRS) )
COVER_OBJ_DIRS := $(patsubst $(IMPL)%, $(COVER_OBJ_DIR)%, $(IMPL_SRC_DIRS) )

TEST_SRC_DIRS := $(shell find $(TEST) -type d \( ! -path '*/.*' \) )
TEST_OBJ_DIRS := $(patsubst $(TEST)%, $(TEST_OBJ_DIR)%, $(TEST_SRC_DIRS) )

GCC := g++ -std=c++0x

EXT_LIBS := -llog4cxx -ljansson
EXT_LIBS_TEST := $(EXT_LIBS) -lcppunit

LIB := $(DIST)/libmr4c.so
LOCAL_EXE := $(EXE)/test_local
TEST_EXE := $(EXE)/run_tests
COVER_EXE := $(EXE)/run_coverage

DOCS_EXE ?= /usr/bin/doxygen
DOCS_CONFIG := ./conf_docs/Doxyfile
# Override docs configs by setting file to DOCS_CONFIG_LOCAL

MR4C_HOME ?= /usr/local/mr4c
NATIVE_HOME := $(MR4C_HOME)/native

#default target
all: clean lib test cover run_tests

clean:
	rm -rf $(OBJ_DIR)
	rm -rf $(DIST)
	rm -rf $(EXE)
	rm -rf $(REPORTS)
	rm -rf $(OUTPUT_DIR)
	rm -rf $(DOCS)

init:
	mkdir -p $(IMPL_OBJ_DIRS)
	mkdir -p $(TEST_OBJ_DIRS)
	mkdir -p $(SUITE_OBJ_DIR)
	mkdir -p $(COVER_OBJ_DIRS)
	mkdir -p $(DIST)
	mkdir -p $(EXE)
	mkdir -p $(REPORTS)
	mkdir -p $(OUTPUT_DIR)
	mkdir -p $(DOCS)


compile: init do_compile

do_compile: compile_src compile_test compile_cover

compile_src: $(IMPL_OBJS)

compile_cover: $(COVER_OBJS)

compile_test: $(TEST_OBJS)


lib: init compile do_lib

do_lib: $(LIB)


test: init compile do_test

do_test: $(TEST_EXE) $(LOCAL_EXE)


cover: init compile do_cover

do_cover: $(COVER_EXE)


run_tests:
	$(TOOLS)/run_tests


docs: init do_docs

# See http://www.stack.nl/~dimitri/doxygen/faq.html for why this works
do_docs: init
	( cat $(DOCS_CONFIG) $(DOCS_CONFIG_LOCAL) ) | $(DOCS_EXE) -


deploy:
	mkdir -p $(NATIVE_HOME)/include
	cp -R $(BIN) $(NATIVE_HOME)
	cp -R $(CONF) $(NATIVE_HOME)
	cp -R $(DIST) $(NATIVE_HOME)
	cp -R $(API)/* $(NATIVE_HOME)/include

remove:
	rm -rf $(NATIVE_HOME)


.PHONY: all, clean, init, \
	compile, do_compile, compile_src, compile_test, compile_cover, \
	test, do_test, cover, do_cover, lib, do_lib, \
	run_tests, docs, do_docs, deploy, remove
	

$(IMPL_OBJS) : $(IMPL_OBJ_DIR)%.o : $(IMPL)%.cpp
	$(GCC) -I$(API) -fPIC -Wall -o $@ -c $< 

$(COVER_OBJS) : $(COVER_OBJ_DIR)%.o : $(IMPL)%.cpp
	$(GCC) -I$(API) -fPIC -Wall --coverage -o $@ -c $< 

$(TEST_OBJS) : $(TEST_OBJ_DIR)%.o : $(TEST)%.cpp
	$(GCC) -I$(API) -I$(TEST_API) -fPIC -Wall -o $@ -c $< 

$(SUITE_OBJ_DIR)/%.o: $(SUITE)/%.cpp
	$(GCC) -I$(API) -I$(TEST_API) -fPIC -Wall -o $@ -c $< 

$(LIB): $(IMPL_OBJS)
	$(GCC) -Wall -rdynamic -shared -o $@ $(IMPL_OBJS) $(EXT_LIBS)

$(LOCAL_EXE): $(IMPL_OBJS) $(TEST_OBJS) $(SUITE_OBJ_DIR)/Local.o
	$(GCC) -Wall -rdynamic -o $@ $(IMPL_OBJS) $(TEST_OBJS) $(SUITE_OBJ_DIR)/Local.o $(EXT_LIBS_TEST)

$(TEST_EXE): $(IMPL_OBJS) $(TEST_OBJS) $(SUITE_OBJ_DIR)/Suite.o
	$(GCC) -Wall -rdynamic -o $@ $(IMPL_OBJS) $(TEST_OBJS) $(SUITE_OBJ_DIR)/Suite.o $(EXT_LIBS_TEST)

$(COVER_EXE): $(COVER_OBJS) $(TEST_OBJS) $(SUITE_OBJ_DIR)/Cover.o
	$(GCC) -Wall -rdynamic --coverage -o $@ $(COVER_OBJS) $(TEST_OBJS) $(SUITE_OBJ_DIR)/Cover.o $(EXT_LIBS_TEST)



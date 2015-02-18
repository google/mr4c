CC=g++
SRC_DIR =./src
LIB=./lib
OBJ_DIR=./objs


MR4C_HOME ?= /usr/local/mr4c
MR4CINC = $(MR4C_HOME)/native/include
MR4CGEOINC = $(MR4C_HOME)/geospatial/include
MR4CLIB = $(MR4C_HOME)/native/lib
MR4CGEOLIB = $(MR4C_HOME)/geospatial/lib

GDALINC = /usr/local/include
GDALLIB = /usr/local/lib

#default target
all: clean init compile lib

clean:
	rm -rf $(LIB)
	rm -rf $(OBJ_DIR)

init:
	mkdir -p $(LIB)
	mkdir -p $(OBJ_DIR)

compile: ./src/content.cpp
	$(CC) -c -I$(MR4CINC) -I$(MR4CGEOINC) -I$(GDALINC) -fPIC -Wall -std=c++0x -o $(OBJ_DIR)/content.o $(SRC_DIR)/content.cpp

lib: $(OBJ_DIR)/content.o
	$(CC) -L$(MR4CLIB) -L$(MR4CGEOLIB) -L$(GDALLIB) -rdynamic -shared -fPIC -o $(LIB)/libContent.so $(OBJ_DIR)/*.o -lmr4c -lmr4cgeo -lgdal


.PHONY: clean, lib, init, all

CC=g++
SRC_DIR =./src
LIB=./lib
OBJ_DIR=./objs


MR4C_HOME ?= /usr/local/mr4c
MR4CINC = $(MR4C_HOME)/native/include
MR4CGEOINC = $(MR4C_HOME)/geospatial/include
MR4CLIB = $(MR4C_HOME)/native/lib

GDALINC = /usr/local/include
GDALLIB = /usr/local/lib

#default target
all: clean init compile libMap libReduce

clean:
	rm -rf $(LIB)
	rm -rf $(OBJ_DIR)

init:
	mkdir -p $(LIB)
	mkdir -p $(OBJ_DIR)

compile: $(SRC_DIR)/map.cpp $(SRC_DIR)/reduce.cpp
	$(CC) -c -I$(MR4CINC)  -I$(MR4CGEOINC) -I$(GDALINC) -I$(SRC_DIR) -fPIC -Wall -std=c++0x -o $(OBJ_DIR)/map.o $(SRC_DIR)/map.cpp
	$(CC) -c -I$(MR4CINC) -I$(MR4CGEOINC) -I$(SRC_DIR) -fPIC -Wall -std=c++0x -o $(OBJ_DIR)/reduce.o $(SRC_DIR)/reduce.cpp

libMap: $(OBJ_DIR)/map.o
	$(CC) -L$(MR4CLIB) -L$(MR4CGEOLIB) -L$(GDALLIB) -rdynamic -shared -fPIC $(OBJ_DIR)/map.o -o $(LIB)/libMap.so -lmr4c -lmr4cgeo -lgdal

libReduce: $(OBJ_DIR)/reduce.o
	$(CC) -L$(MR4CLIB) -rdynamic -shared -fPIC -o $(LIB)/libReduce.so $(OBJ_DIR)/reduce.o -lmr4c

.PHONY: clean, libMap, libReduce, init, all

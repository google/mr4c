CC=g++
SRC_DIR=./src
LIB=./lib
OBJ_DIR=./objs
TARGET=RandomAccess

MR4C_HOME ?= /usr/local/mr4c
MR4CINC = $(MR4C_HOME)/native/include
MR4CLIB = $(MR4C_HOME)/native/lib

#default target
all: clean init compile lib

clean:
	rm -rf $(LIB)
	rm -rf $(OBJ_DIR)

init:
	mkdir -p $(LIB)
	mkdir -p $(OBJ_DIR)

compile:
	$(CC) -c -I$(MR4CINC)  -I$(MR4CGEOINC) -I$(SRC_DIR) -fPIC -Wall -std=c++0x -o $(OBJ_DIR)/$(TARGET).o $(SRC_DIR)/$(TARGET).cpp

lib: $(OBJ_DIR)/$(TARGET).o
	$(CC) -L$(MR4CLIB) -L$(MR4CGEOLIB) -L$(GDALLIB) -rdynamic -shared -fPIC $(OBJ_DIR)/$(TARGET).o -o $(LIB)/lib$(TARGET).so -lmr4c

.PHONY: clean, compile,lib, init, all

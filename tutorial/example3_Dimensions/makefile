CC=g++
SRC_DIR =./src
LIB = ./lib
OBJ_DIR = ./objs

SRCS = $(shell find $(SRC_DIR) -name *.cpp)
OBJS = $(patsubst $(SRC_DIR)%.cpp, $(OBJ_DIR)%.o, $(SRCS) )

MR4C_HOME ?= /usr/local/mr4c
MR4CINC = $(MR4C_HOME)/native/include
MR4CLIB = $(MR4C_HOME)/native/lib

#default target
all: clean init compile lib

clean:
	rm -rf $(OBJ_DIR)/*
	rm -rf $(LIB)/*
	rm -rf $(OBJ_DIR)
	rm -rf $(LIB)

init:
	mkdir -p $(OBJ_DIR)
	mkdir -p $(LIB)

compile: ./src/dimensions.cpp
	$(CC) -c -I$(MR4CINC) -o ./objs/dimensions.o -fPIC -Wall -std=c++0x ./src/dimensions.cpp

lib: ./objs/dimensions.o
	$(CC) -L$(MR4CLIB) -lmr4c -rdynamic -shared -fPIC -std=c++0x -o ./lib/libDimensions.so ./objs/dimensions.o


.PHONY: clean, lib, init, all

CC=g++
SRC_DIR =./src
LIB = ./lib
OBJ_DIR = ./objs

SRCS = $(shell find $(SRC_DIR) -name *.cpp)
OBJS = $(patsubst $(SRC_DIR)%.cpp, $(OBJ_DIR)%.o, $(SRCS) )

MR4C_HOME ?= /usr/local/mr4c
MR4CINC = $(MR4C_HOME)/native/include
MR4CLIB = $(MR4C_HOME)/native/lib
JANSSONINC = /usr/include
JANSSONLIB = /usr/lib
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

compile: ./src/json.cpp
	$(CC) -c -I$(MR4CINC) -o ./objs/json.o -fPIC -Wall -std=c++0x ./src/json.cpp

lib: ./objs/json.o
	$(CC) -I$JANSSONINC -L$(JANSSONINC) -ljansson -L$(MR4CLIB) -lmr4c -rdynamic -shared -fPIC -std=c++0x -o ./lib/libJSON.so ./objs/json.o


.PHONY: clean, lib, init, all

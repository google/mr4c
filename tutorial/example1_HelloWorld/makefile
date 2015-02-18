CC=g++
SRC_DIR =./src
LIB_DIR = ./lib
OBJ_DIR = ./objs

SRCS = $(shell find $(SRC_DIR) -name *.cpp)
OBJS = $(patsubst $(SRC_DIR)%.cpp, $(OBJ_DIR)%.o, $(SRCS) )

MR4C_HOME ?= /usr/local/mr4c
MR4CINC = $(MR4C_HOME)/native/include
MR4CLIB = $(MR4C_HOME)/native/lib

#default target
all: clean init compile lib

init:
	mkdir -p $(OBJ_DIR)
	mkdir -p $(LIB_DIR)

compile: ./src/helloworld.cpp
	$(CC) -c -I$(MR4CINC) -o ./objs/helloworld.o -fPIC -Wall -std=c++0x ./src/helloworld.cpp

lib: ./objs/helloworld.o
	$(CC) -L$(MR4CLIB) -lmr4c -rdynamic -shared -fPIC -std=c++0x -o ./lib/libHelloWorld.so ./objs/helloworld.o

clean:
	rm -rf $(OBJ_DIR)
	rm -rf $(LIB_DIR)


.PHONY: clean, init, compile, lib, all

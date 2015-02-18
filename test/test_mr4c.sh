#!/bin/bash

set -e 

echo "***********************"
echo "Running MR4C Test"
echo "***********************"

#add dist to LD_LIBRARY_PATH
export LD_LIBRARY_PATH=./dist:$LD_LIBRARY_PATH

bin/configure_mr4cref_input
bin/run_mr4cref
bin/check_mr4cref_output

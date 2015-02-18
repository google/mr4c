#!/bin/bash

export MR4C_SITE=./site.json;
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$PWD/lib:/usr/local/lib

set -e

#stage data

if ! hadoop fs -test -d skysatIn
then
   hadoop fs -mkdir skysatIn
   hadoop fs -mkdir skysatReduce
   hadoop fs -put ./input/* skysatIn
fi

#run mr4c
#mapper
mr4c_hadoop ./map.json -Htasks=2 -Hcores.min=1 -Hcores.max=2 -Hmemory.min=1024 -Hmemory.max=2048

#pause between map and reduce
sleep 1

#reducer
mr4c_hadoop ./reduce.json -Htasks=1 -Hcores.min=1 -Hcores.max=2 -Hmemory.min=1024 -Hmemory.max=2048

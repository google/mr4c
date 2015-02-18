#!/bin/bash

export MR4C_SITE=./site.json;
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$PWD/lib:/usr/local/lib

#stage data
if [ ! `hadoop fs -test -d skysatIn` ] ;
then
   hadoop fs -mkdir skysatIn
   hadoop fs -mkdir skysatOut
   hadoop fs -put ./input/* skysatIn
fi
#run mr4c
mr4c_hadoop ./RandomAccess.json -Htasks=2 -Hcores.min=1 -Hcores.max=2 -Hmemory.min=1024 -Hmemory.max=2048


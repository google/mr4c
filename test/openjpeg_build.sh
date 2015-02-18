#!/bin/bash

VERSION="openjpeg_v1_4_sources_r697"
FILE="${VERSION}.tgz"
DIR="openjpeg"
URL="https://openjpeg.googlecode.com/files/${FILE}"

wget --no-check-certificate ${URL}

if [ -f "$FILE" ]; then
    tar -xzf ${FILE}
    mv ${VERSION} ${DIR}
    cd ${DIR}
    ./configure --prefix=/usr/local
    make
    sudo make install
    cd -
    rm ${FILE}
else
    echo "File ${FILE} was not downloaded from ${URL}"
    exit 1
fi

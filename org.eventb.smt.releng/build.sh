#!/bin/bash
#
#  Builds feature from fetched sources.
#

PATH=/bin:/usr/bin

. ./config.sh

SRC_PATH=$(pwd)/src
WORK_PATH=$(pwd)/work
RESULT_PATH=$(pwd)/result

BUILDER="$SRC_PATH/$RELENG/build"

CONFIGS='
    linux,gtk,x86 &
    linux,gtk,x86_64 &
    win32,win32,x86 &
    win32,win32,x86_64 &
    macosx,cocoa,x86_64'

unpackArchives() {
    echo "Extracting Eclipse and Rodin archives"
    tar zxf "../archives/$ECLIPSE_SDK"
    unzip -oq "../archives/$ECLIPSE_DELTA"
    ECLIPSE_HOME="$WORK_PATH/eclipse"

    unzip -oq "../archives/$RODIN_TARGET"
    TARGET_PATH="$WORK_PATH/rodin"
}

eclipseFile() {
    ls "$ECLIPSE_HOME/"$1
}

# ----------------------------------------------------------------------
#  Main program
# ----------------------------------------------------------------------
createDir work
cd work
unpackArchives
. "$BUILDER/runBuilder.sh"
#cp archive "../result/$BUILD_FULLNAME-sources.zip"

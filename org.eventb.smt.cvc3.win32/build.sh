#!/bin/bash
#
#  Cross-compilation of cvc3 to win32
#
#  Meant to be used on Ubuntu 10.04.4 with package i586-mingw32msvc installed
#

BASE_DIR="~/cvc3"
TARGET=i586-mingw32msvc

usage() {
  echo "Usage: `basename $0` <gmp-x.x.x.tar.gz> <cvc3-x.x.x.tar.gz>" >&2
  exit 1
}

compile_gmp() {
    tar xzf $GMP_ARCHIVE
    cd $GMP_DIR
    ./configure --prefix=$BASE_DIR/$TARGET --host=$TARGET
    make
    make install
    cd ..
}

compile_cvc3() {
    tar xzf $CVC3_ARCHIVE
    cd $CVC3_DIR
    rm -v src/sat/xchaff*
    ./configure --disable-zchaff --host=$TARGET \
	--with-extra-includes=$BASE_DIR/$TARGET/include \
	--with-extra-libs=$BASE_DIR/$TARGET/lib \
	CPPFLAGS="-D_MSC_VER -D_LINUX_WINDOWS_CROSS_COMPILE"
    make
    #cd test
    #make
    cd ..
}

test $# -eq 2 || usage
GMP_ARCHIVE=$1
CVC3_ARCHIVE=$2

GMP_DIR="${GMP_ARCHIVE%%.tar.gz}"
CVC3_DIR="${CVC3_ARCHIVE%%.tar.gz}"

compile_gmp
compile_cvc3

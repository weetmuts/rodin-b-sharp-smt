#!/bin/bash
#
#  Configuration for building a new feature from Subversion
#

# Name of build, used to name the resulting files
BUILD_NAME=SMT_Solvers

# User visible identity of the build.  Should be the same as the feature
# version.
BUILD_ID=1.0.0

# Subversion revision to use for the build
SVNREV=14671

# Root URL of the Subversion repository
SVN_ROOT=https://rodin-b-sharp.svn.sourceforge.net/svnroot/rodin-b-sharp

# Project Set containing the source project URLs
SVN_PSF=$SVN_ROOT/trunk/SMT/org.eventb.smt.releng/org.eventb.smt-src.psf

# Release engineering project containing the build parameters
RELENG=org.eventb.smt.releng

# Name of archive containing the Rodin target platform in archives directory
RODIN_TARGET=rodin-2.5-dev.zip

# Name of archive containing the Eclipse SDK in archives directory
ECLIPSE_SDK=eclipse-SDK-3.7.2-linux-gtk.tar.gz

# Name of archive containing the Eclipse delta pack in archives directory
ECLIPSE_DELTA=eclipse-3.7.2-delta-pack.zip

# ----------------------------------------------------------------------
# There is no need to change anything below
# ----------------------------------------------------------------------

# Full build version
BUILD_VERSION=$BUILD_ID.r$SVNREV

# Full build name
BUILD_FULLNAME="$BUILD_NAME-$BUILD_ID"

fatal() {
    echo "$@" >&2
    exit 1
}

checkRevision() {
    val="$1"
    if expr "$val" : '^[0-9][0-9]*$' >/dev/null; then
	: OK
    else
	fatal "Invalid SVN revision number: $val"
    fi
}

createDir() {
    local dir="$1"
    [ -d "$dir" ] && {
	echo "Directory '$dir' already exists." >&2
	read -p "Type <CR> to continue, CTRL-C to interrupt" answer
	rm -rf "$dir"
    }
    mkdir "$dir" || fatal "Can't create directory '$dir'."
}

checkRevision "$SVNREV"

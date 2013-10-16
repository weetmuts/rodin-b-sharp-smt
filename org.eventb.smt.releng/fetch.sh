#!/bin/sh
#
#  Fetch sources from the Rodin Subversion repository on SourceForge.
#

PATH=/bin:/usr/bin

. ./config.sh

LOCAL_PSF=$(basename $PSF_PATH)

fetchAllProjects() {
    mkdir gitrepo || fatal "Some sources have already been checked out."
    git clone $GIT_ROOT gitrepo
    cd gitrepo
    git checkout -q $GIT_COMMIT

    cat $PSF_PATH | 
    grep "^<project" |
    sed -e 's/.*\,//' -e 's/\".*//' |
    xargs -i cp -R {} ../src

    cd ..
}

archive() {
    local path="$1"
    echo "Creating archive $(basename $path)"
    rm -f "$path"
    zip -qr "$path" * -x "$LOCAL_PSF"
}

# ----------------------------------------------------------------------
#  Main program
# ----------------------------------------------------------------------
createDir result
if [ $# -eq 0 ]; then
    createDir src
    fetchAllProjects
fi
cd src
archive "../result/$BUILD_FULLNAME-sources.zip"

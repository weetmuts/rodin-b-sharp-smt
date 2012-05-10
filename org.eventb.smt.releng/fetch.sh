#!/bin/sh
#
#  Fetch sources from the Rodin Subversion repository on SourceForge.
#

PATH=/bin:/usr/bin

. ./config.sh

LOCAL_PSF=$(basename $SVN_PSF)

fetchProjectSet() {
    wget -nv $SVN_PSF || fatal "Can't fetch project set file."
}

fetchAllProjects() {
    awk -F, '$1 == "<project reference=\"0.9.3" { sub("\"/>", "", $3); print $2, $3; }' $LOCAL_PSF |
	while read url dir; do
	    fetchProject "$url" "$dir"
	done
}

fetchOneProject() {
    dir="$1"
    awk -F, '$3 == "'"$dir"'\"/>" { print $2; }' $LOCAL_PSF |
	while read url; do
	    fetchProject "$url" "$dir"
	done
}

fetchProject() {
    url="$1"
    dir="$2"
    echo "Fetching $dir"
    rm -rf "$dir"
    svn -q export -r $SVNREV "$url" "$dir"
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
    cd src
    fetchProjectSet
    fetchAllProjects
else
    cd src
    for p; do fetchOneProject "$p"; done
fi
archive "../result/$BUILD_FULLNAME-sources.zip"

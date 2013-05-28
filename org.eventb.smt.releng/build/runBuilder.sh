#!/bin/bash
#
#  Launches a PDE feature build from fetched sources.
#
#  This script is not supposed to be run directly, but rather launched by the
#  "build.sh" script in the main folder of this plug-in.
#

launcher=$(eclipseFile "plugins/org.eclipse.equinox.launcher_*.jar")
script=$(eclipseFile "plugins/org.eclipse.pde.build_*/scripts/build.xml")
java -jar "$launcher" \
    -application org.eclipse.ant.core.antRunner \
    -data "$WORK_PATH/ws" \
    -buildfile "$script" \
    -Dbuilder="$BUILDER" \
    -DbuildDirectory="$WORK_PATH" \
    -DbaseLocation="$ECLIPSE_HOME" \
    -DpluginPath="$TARGET_PATH" \
    -DforceContextQualifier="$GIT_COMMIT" \
    -Dconfigs="$CONFIGS" \
    -Dp2.gathering=true \
    -Dp2.repo.archive.path="$RESULT_PATH/$BUILD_FULLNAME-repo.zip"
#	-verbose 

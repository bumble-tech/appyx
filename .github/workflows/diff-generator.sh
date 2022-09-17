#!/bin/bash
# Forked from https://github.com/usefulness/dependency-tree-diff-action/blob/master/entrypoint.sh
# This version allows running a different dependency task which is necessary when mixing java/android modules.

cd "$INPUT_BUILD_ROOT_DIR"

./gradlew dependencyDiffCreate -PfolderName=old
git fetch --force origin "$INPUT_BASEREF":"$INPUT_BASEREF" --no-tags
git switch --force "$INPUT_BASEREF"
./gradlew dependencyDiffCreate -PfolderName=new

./gradlew dependencyDiffRun -PfolderName1=old -PfolderName2=old

#diff="${diff//'%'/'%25'}"
#diff="${diff//$'\n'/'%0A'}"
#diff="${diff//$'\r'/'%0D'}"
#echo "::set-output name=text-diff::$diff"


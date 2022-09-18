#!/bin/bash -el
# Forked from https://github.com/usefulness/dependency-tree-diff-action/blob/master/entrypoint.sh
# This version allows running a different dependency task which is necessary when mixing java/android modules.

wget "https://github.com/JakeWharton/dependency-tree-diff/releases/download/$INPUT_VERSION/dependency-tree-diff.jar" -q -O dependency-tree-diff.jar

cd "$INPUT_BUILD_ROOT_DIR"

baselineDependenciesDir=build/release-dependencies-diff/baseline-dependencies
dependenciesDir=build/release-dependencies-diff/dependencies

# Determine the dependencies of the PR before switching to the base branch.
./gradlew releaseDependenciesCreateFiles --outputPath=$dependenciesDir

# Switch to base ref and determine the dependencies
git fetch --force origin "$INPUT_BASEREF":"$INPUT_BASEREF" --no-tags
git switch --force "$INPUT_BASEREF"
./gradlew releaseDependenciesCreateFiles --outputPath=$baselineDependenciesDir

# Note: We execute releaseDependenciesDiffFiles on the base branch, so if you update this
# gradle task you may see unexpected results.
./gradlew releaseDependenciesDiffFiles \
  --baselineDependenciesPath=$baselineDependenciesDir \
  --dependenciesPath=$dependenciesDir

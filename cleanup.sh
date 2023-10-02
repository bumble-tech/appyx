#!/bin/sh
rm -rf .idea
./gradlew clean
rm -rf .gradle
rm -rf build
rm -rf */build
rm -rf demos/appyx-interactions/iosApp/iosApp.xcworkspace
rm -rf demos/appyx-interactions/iosApp/Pods
rm -rf demos/appyx-interactions/iosApp/iosApp.xcodeproj/project.xcworkspace
rm -rf demos/appyx-interactions/iosApp/iosApp.xcodeproj/xcuserdata

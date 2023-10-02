<img src="https://user-images.githubusercontent.com/238198/177164121-3aa4d19d-7714-4f2e-af12-7d3335b43f9c.png" width="75" />

# Appyx

[![Build](https://github.com/bumble-tech/appyx/actions/workflows/build.yml/badge.svg)](https://github.com/bumble-tech/appyx/actions/workflows/build.yml)
![Maven Central](https://img.shields.io/maven-central/v/com.bumble.appyx/core)
![Maven Central](https://img.shields.io/maven-central/v/com.bumble.appyx/appyx-interactions)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

## Project page
[https://bumble-tech.github.io/appyx](https://bumble-tech.github.io/appyx)

## License

<pre>
Copyright 2021 Bumble.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>

## Setting up the environment for execution

> **Warning**
> For running the iOS target, you need a Mac with macOS to write and run iOS-specific code on simulated or real devices.
> This is an Apple requirement.
> 
> This guide is tweaked and inspired from the [Compose-Multiplatform-iOS template](https://github.com/JetBrains/compose-multiplatform-ios-android-template).
> To work with this template, you need the following:

* A machine running a recent version of macOS
* [Xcode](https://apps.apple.com/us/app/xcode/id497799835)
* [Android Studio](https://developer.android.com/studio)
* The [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
* The [CocoaPods dependency manager](https://kotlinlang.org/docs/native-cocoapods.html)

### Check your environment

Before you start, use the [KDoctor](https://github.com/Kotlin/kdoctor) tool to ensure that your development environment is configured correctly:

1. Install KDoctor with [Homebrew](https://brew.sh/):

    ```text
    brew install kdoctor
    ```

2. Run KDoctor in your terminal:

    ```text
    kdoctor
    ```

   If everything is set up correctly, you'll see valid output:

   ```text
   Environment diagnose (to see all details, use -v option):
   [✓] Operation System
   [✓] Java
   [✓] Android Studio
   [✓] Xcode
   [✓] Cocoapods
   
   Conclusion:
     ✓ Your system is ready for Kotlin Multiplatform Mobile development!
   ```

Otherwise, KDoctor will highlight which parts of your setup still need to be configured and will suggest a way to fix them.

## The project structure

Open the project in Android Studio and switch the view from **Android** to **Project** to see all the files and targets belonging to the project.
The :demos module contain the sample targets for [appyx-interactions](https://bumble-tech.github.io/appyx/interactions/) and [appyx-navigation](https://bumble-tech.github.io/appyx/navigation/).

These modules follow the standard compose multiplatform project structure:

### common

This is a Kotlin module that contains the logic common for Android, Desktop, iOS and web applications, that is, the code you share between platforms.

### android

This is a Kotlin module that builds into an Android application. It uses Gradle as the build system.
The `android` module depends on and uses the `common` module as a regular Android library.

### ios

This is an Xcode project that builds into an iOS application.
The `:demos:appyx-navigation` module depends on and uses the `:demos:appyx-navigation:common` module as a CocoaPods dependency.

## Run your application

### On Android

To run your application on an Android emulator:

1. Ensure you have an Android virtual device available. Otherwise, [create one](https://developer.android.com/studio/run/managing-avds#createavd).
2. In the list of run configurations, select `demos.appyx-navigation.android` or `demos.appyx-interactions.android`.
3. Choose your virtual/physical device and click **Run**.

### On iOS

#### Running on a simulator

To run your application on an iOS simulator in Android Studio, modify the `iOS` run configuration:

1. In the list of run configurations, select **Edit Configurations**:

2. Navigate to **iOS Application** | **iosApp**.
3. Select the desired `.xcworkspace` file under `XCode project file` which can be found in `/demos/appyx-interactions/iosApp/iosApp.xcworkspace` or
`/demos/appyx-navigation/iosApp/iosApp.xcworkspace`
3. In the **Execution target** list, select your target device. Click **OK**:

4. The `iosApp` run configuration is now available. Click **Run** next to your virtual device:

### On Desktop

To run the application as a JVM target on desktop:

1. In the list of run configurations, select **Edit Configurations**:
2. Click **Add new configuration** and select **Gradle**.
3. Set `run` configuration under `Run`.
4. Select the desired target under `Gradle project` to be executed (for example: `appyx:demos:appyx-navigation:desktop`).
5. The desktop configuration for the desired target is now available. Click **Run** to execute.

### On Web

To run the application on web:

1. In the list of run configurations, select **Edit Configurations**:
2. Click **Add new configuration** and select **Gradle**.
3. Set `jsBrowserDevelopmentRun` under `Run`.
4. Select the desired target under `Gradle project` to be executed (for example: `appyx:demos:appyx-navigation:web`).
5. The web configuration for the desired target is now available. Click **Run** to execute.


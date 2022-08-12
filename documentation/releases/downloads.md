# Downloads

You need to specify the following repositories in your Gradle files:

```groovy
repositories {
    mavenCentral()
}
```


## Core dependencies

```groovy
dependencies {
    // Core
    implementation "com.bumble.appyx:core:$version"

    // Test rules and utility classes for testing on Android
    androidTestImplementation "com.bumble.appyx:testing-ui:$version"

    // Utility classes for unit testing
    testImplementation "com.bumble.appyx:testing-unit-common:$version"
    
    // Test rules and utility classes for unit testing using JUnit4
    testImplementation "com.bumble.appyx:testing-junit4:$version"

    // Test extensions and utility classes for unit testing using JUnit5
    testImplementation "com.bumble.appyx:testing-junit5:$version"
}
```


## Addons

```groovy
dependencies {
    // Additional routing sources (such as Tiles, Promoter carousel and Modal)
    implementation "com.bumble.appyx:routing-source-addons:$version"
}
```


## Interop with other libraries

```groovy
dependencies {
    // Optional support for RxJava 2
    implementation "com.bumble.appyx:interop-rx2:$version"

    // Optional interoperability layer between Appyx and badoo/RIBs
    // You have to add https://jitpack.io repository to use it because badoo/RIBs is hosted there
    implementation "com.bumble.appyx:interop-ribs:$version"

}
```

## Snapshot

Snapshot version is available for all modules, use the provided repository url and `main-SNAPSHOT` version.

```groovy
repositories {
    maven { url = 'https://s01.oss.sonatype.org/content/repositories/snapshots/' }
}

dependencies {
    implementation "com.bumble.appyx:core:main-SNAPSHOT"
}
```

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
    
    // Test rules and utility classes for unit testing
    testImplementation "com.bumble.appyx:testing-unit:$version"
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

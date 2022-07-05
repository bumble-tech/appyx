# Downloads

You need to specify the following repositories in your Gradle files:

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

JitPack repository requirement will be eliminated soon.

## Core dependencies

```groovy
dependencies {
    // Core
    implementation "com.bumble.appyx:core:$version"

    // Test rules and utility classes for testing on Android
    androidTestImplementation "com.bumble.appyx:testing-ui:$version"
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
    implementation "com.bumble.appyx:interop-ribs:$version"

}
```

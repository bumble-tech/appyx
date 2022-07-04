# Downloads

You need to specify the following repositories in your Gradle files:

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

JitPack repository requirement will be eliminated soon.

## Dependencies

```groovy
dependencies {
    // Minimum requirement for using Appyx
    implementation "com.bumble.appyx:core:$version"

    // Optional support for RxJava 2
    implementation "com.bumble.appyx:interop-rx2:$version"

    // Optional interoperability layer between Appyx and RIBs
    implementation "com.bumble.appyx:interop-ribs:$version"

    // Optional additional routing sources (such as Tiles, Promoter Carousel and Modal)
    implementation "com.bumble.appyx:routing-source-addons:$version"

    // Contains test rules and utility classes for testing on Android
    androidTestImplementation "com.bumble.appyx:testing-ui:$version"
}
```

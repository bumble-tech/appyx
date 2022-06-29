# Downloads

You need to specify the following repositories in your Gradle files:

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

JitPack repository requirement will be eliminated soon.

## Library core

```groovy
dependencies {
    implementation "com.bumble.appyx:core:$version"
    
    androidTestImplementation "com.bumble.appyx:testing:$version"
}
```

## Routing source addons




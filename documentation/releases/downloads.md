# Downloads

## Latest version

![Maven Central](https://img.shields.io/maven-central/v/com.bumble.appyx/appyx-navigation)

## Repository

```groovy
repositories {
    mavenCentral()
}
```

## Appyx Navigation

```groovy
dependencies {
    // Android-only. Multiplatform is coming soon.
    implementation "com.bumble.appyx:appyx-navigation:$version"
}
```

## Appyx Interactions

```groovy
dependencies {
    // Multiplatform
    implementation "com.bumble.appyx:appyx-interactions:$version"
    
    // Platform-specific
    implementation "com.bumble.appyx:appyx-interactions-android:$version"
    implementation "com.bumble.appyx:appyx-interactions-desktop:$version"
    implementation "com.bumble.appyx:appyx-interactions-js:$version"
}
```

## Appyx Components

### Back stack

```groovy
dependencies {
    // Pick one (for your platform)
    implementation "com.bumble.appyx:backstack-android:$version"
    implementation "com.bumble.appyx:backstack-desktop:$version"
    implementation "com.bumble.appyx:backstack-js:$version"
}
```

### Spotlight

```groovy
dependencies {
    // Pick one (for your platform)
    implementation "com.bumble.appyx:spotlight-android:$version"
    implementation "com.bumble.appyx:spotlight-desktop:$version"
    implementation "com.bumble.appyx:spotlight-js:$version"
}
```

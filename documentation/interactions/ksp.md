---
title: Appyx Interactions – KSP setup
---

# Appyx Interactions – KSP setup

Defining `TargetUiStates` is easy, as demonstrated in [UI representation](ui-representation.md)

```kotlin
@MutableUiStateSpecs
class TargetUiState(
    val position: Position.Target,
    val rotation: Rotation.Target,
    val backgroundColor: BackgroundColor.Target,
)
```

However, for every `TargetUiState`, there needs to exist a corresponding `MutableUiState` class containing the animation code. 

By adding the `@MutableUiStateSpecs` annotation, if you follow the below setup guide, you can have the Appyx KSP mutable ui state processor generate this class for you automatically.


## Setup

Works with **Kotlin 1.8.10**. For our migration to **Kotlin 1.9** please check:

- [#547](https://github.com/bumble-tech/appyx/issues/547) - Upgrade Compose Multiplatform / Kotlin


### Main `build.gradle`

```kotlin
plugins {
    id("com.google.devtools.ksp") version libs.versions.ksp.get() apply false
    // Alternatively: 
    // id("com.google.devtools.ksp") version '1.8.0-1.0.8' apply false
}
```

### App `build.gradle`

```kotlin
plugins {
    id("com.google.devtools.ksp") 
}

composeOptions {
    kotlinCompilerExtensionVersion = '1.4.3'
}

dependencies {
    ksp("com.bumble.appyx:appyx-processor:{latest version}")
}
```

---
title: Downloads
---

# Downloads

## Latest version

![Maven Central](https://img.shields.io/maven-central/v/com.bumble.appyx/appyx-navigation)

## Repository

```kotlin
repositories {
    mavenCentral()
}
```


## Appyx Navigation

Adding the gradle dependency in a non-multiplatform project:

```kotlin
dependencies {
    // Platform-specific (pick the right one for your platform)
    implementation("com.bumble.appyx:appyx-navigation-android:$version")
    implementation("com.bumble.appyx:appyx-navigation-desktop:$version")
    implementation("com.bumble.appyx:appyx-navigation-js:$version")

    // For iOS, you need separate dependencies for Simulator, x86 & Arm
    implementation("com.bumble.appyx:appyx-navigation-iossimulatorarm64:$version")
    implementation("com.bumble.appyx:appyx-navigation-iosx64:$version")
    implementation("com.bumble.appyx:appyx-navigation-iosarm64:$version")
}
```

Adding the gradle dependency in a multiplatform project:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.bumble.appyx:appyx-navigation:$version")
            }
        }
    }
}
```

## Appyx Interactions

Adding the gradle dependency in a non-multiplatform project:

```kotlin
dependencies {
    // Platform-specific (pick the right one for your platform)
    implementation("com.bumble.appyx:appyx-interactions-android:$version")
    implementation("com.bumble.appyx:appyx-interactions-desktop:$version")
    implementation("com.bumble.appyx:appyx-interactions-js:$version")

    // For iOS, you need separate dependencies for Simulator, x86 & Arm
    implementation("com.bumble.appyx:appyx-interactions-iossimulatorarm64:$version")
    implementation("com.bumble.appyx:appyx-interactions-iosx64:$version")
    implementation("com.bumble.appyx:appyx-interactions-iosarm64:$version")
}
```

Adding the gradle dependency in a multiplatform project:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.bumble.appyx:appyx-interactions:$version")
            }
        }
    }
}
```

## Appyx Components

### Back stack

Adding the gradle dependency in a non-multiplatform project:

```kotlin
dependencies {
    // Platform-specific (pick the right one for your platform)
    implementation("com.bumble.appyx:backstack-android:$version")
    implementation("com.bumble.appyx:backstack-desktop:$version")
    implementation("com.bumble.appyx:backstack-js:$version")

    // For iOS, you need separate dependencies for Simulator, x86 & Arm
    implementation("com.bumble.appyx:backstack-iossimulatorarm64:$version")
    implementation("com.bumble.appyx:backstack-iosx64:$version")
    implementation("com.bumble.appyx:backstack-iosarm64:$version")
}
```

Adding the gradle dependency in a multiplatform project:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Use api, not implementation!
                api("com.bumble.appyx:backstack:$version")
            }
        }

    }
}
```

### Spotlight

Adding the gradle dependency in a non-multiplatform project:

```kotlin
dependencies {
    // Platform-specific (pick the right one for your platform)
    implementation("com.bumble.appyx:spotlight-android:$version")
    implementation("com.bumble.appyx:spotlight-desktop:$version")
    implementation("com.bumble.appyx:spotlight-js:$version")

    // For iOS, you need separate dependencies for Simulator, x86 & Arm
    implementation("com.bumble.appyx:spotlight-iossimulatorarm64:$version")
    implementation("com.bumble.appyx:spotlight-iosx64:$version")
    implementation("com.bumble.appyx:spotlight-iosarm64:$version")
}
```

Adding the gradle dependency in a multiplatform project:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Use api, not implementation!
                api("com.bumble.appyx:spotlight:$version")
            }
        }

    }
}
```

## Utils and interop with other libraries

### Material 3 support

Support for [Navigation bar](https://m3.material.io/components/navigation-bar/overview), [Navigation rail](https://m3.material.io/components/navigation-rail/overview) to use easily together with **Appyx Navigation**.

See more in [Material 3 support](../navigation/features/material3.md).

Adding the gradle dependency in a non-multiplatform project:

```kotlin
dependencies {
    // Platform-specific (pick the right one for your platform)
    implementation("com.bumble.appyx:utils-material3-android:$version")
    implementation("com.bumble.appyx:utils-material3-desktop:$version")
    implementation("com.bumble.appyx:utils-material3-js:$version")
    implementation("com.bumble.appyx:utils-material3-iosarm64:$version")
    implementation("com.bumble.appyx:utils-material3-iossimulatorarm64:$version")
    implementation("com.bumble.appyx:utils-material3-iosx64:$version")
}
```

Adding the gradle dependency in a multiplatform project:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Use api, not implementation!
                api("com.bumble.appyx:utils-material3:$version")
            }
        }
    }
}
```

### RxJava 2

```kotlin
dependencies {
    // Optional support for RxJava 2/3
    implementation("com.bumble.appyx:utils-interop-rx2:$version")
}
```

### RxJava 3

```kotlin
dependencies {
    implementation("com.bumble.appyx:utils-interop-rx3:$version")
}
```

### badoo/RIBs

```kotlin
repositories {
    // Don't forget to add this, since badoo/RIBs is hosted on jitpack:
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.bumble.appyx:utils-interop-ribs:$version")
}
```

### RIBs like helpers

Adds client code helper classes like `Builder`, `SimpleBuilder`, and `Interactor`

```kotlin
dependencies {
    implementation("com.bumble.appyx:utils-ribs-helpers:$version")
}
```


### Testing

```kotlin
// Test rules and utility classes for testing on Android
debugImplementation("com.bumble.appyx:utils-testing-ui-activity:$version")
androidTestImplementation("com.bumble.appyx:utils-testing-ui:$version")

// Utility classes for unit testing
testImplementation("com.bumble.appyx:utils-testing-unit-common:$version")

// Test rules and utility classes for unit testing using JUnit4
testImplementation("com.bumble.appyx:utils-testing-junit4:$version")

// Test extensions and utility classes for unit testing using JUnit5
testImplementation("com.bumble.appyx:utils-testing-junit5:$version")
```

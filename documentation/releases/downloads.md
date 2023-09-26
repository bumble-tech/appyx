---
title: Downloads
---

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

Adding the gradle dependency in a non-multiplatform project:

```groovy
dependencies {
    // Platform-specific (pick the right one for your platform)
    implementation "com.bumble.appyx:appyx-navigation-android:$version"
    implementation "com.bumble.appyx:appyx-navigation-desktop:$version"
    implementation "com.bumble.appyx:appyx-navigation-js:$version"
}
```

Adding the gradle dependency in a multiplatform project:

```groovy
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation "com.bumble.appyx:appyx-navigation:$version"
            }
        }
    }
}
```

## Appyx Interactions

Adding the gradle dependency in a non-multiplatform project:

```groovy
dependencies {
    // Platform-specific (pick the right one for your platform)
    implementation "com.bumble.appyx:appyx-interactions-android:$version"
    implementation "com.bumble.appyx:appyx-interactions-desktop:$version"
    implementation "com.bumble.appyx:appyx-interactions-js:$version"
}
```

Adding the gradle dependency in a multiplatform project:

```groovy
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation "com.bumble.appyx:appyx-interactions:$version"
            }
        }
    }
}
```

## Appyx Components

### Back stack

Adding the gradle dependency in a non-multiplatform project:

```groovy
dependencies {
    // Platform-specific (pick the right one for your platform)
    implementation "com.bumble.appyx:backstack-android:$version"
    implementation "com.bumble.appyx:backstack-desktop:$version"
    implementation "com.bumble.appyx:backstack-js:$version"
}
```

Adding the gradle dependency in a multiplatform project:

```groovy
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Use api, not implementation!
                api "com.bumble.appyx:backstack:$version"
            }
        }

    }
}
```

### Spotlight

Adding the gradle dependency in a non-multiplatform project:

```groovy
dependencies {
    // Platform-specific (pick the right one for your platform)
    implementation "com.bumble.appyx:spotlight-android:$version"
    implementation "com.bumble.appyx:spotlight-desktop:$version"
    implementation "com.bumble.appyx:spotlight-js:$version"
}
```

Adding the gradle dependency in a multiplatform project:

```groovy
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Use api, not implementation!
                api "com.bumble.appyx:spotlight:$version"
            }
        }

    }
}
```

## Utils and interop with other libraries

### RxJava 2

```groovy
dependencies {
    // Optional support for RxJava 2/3
    implementation "com.bumble.appyx:utils-interop-rx2:$version"
}
```

### RxJava 3

```groovy
dependencies {
    implementation "com.bumble.appyx:utils-interop-rx3:$version"
}
```

### badoo/RIBs

```groovy
repositories {
    // Don't forget to add this, since badoo/RIBs is hosted on jitpack:
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation "com.bumble.appyx:utils-interop-ribs:$version"

}
```


### Testing

```groovy
    // Test rules and utility classes for testing on Android
    debugImplementation "com.bumble.appyx:utils-testing-ui-activity:$version"
    androidTestImplementation "com.bumble.appyx:utils-testing-ui:$version"

    // Utility classes for unit testing
    testImplementation "com.bumble.appyx:utils-testing-unit-common:$version"
    
    // Test rules and utility classes for unit testing using JUnit4
    testImplementation "com.bumble.appyx:utils-testing-junit4:$version"

    // Test extensions and utility classes for unit testing using JUnit5
    testImplementation "com.bumble.appyx:utils-testing-junit5:$version"
```

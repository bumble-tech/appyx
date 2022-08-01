import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

buildscript {
  repositories {
    google()
    mavenCentral()
  }
}

plugins {
  `java-gradle-plugin`
  `kotlin-dsl`
  `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
  implementation(libs.android.plugin)
  implementation(libs.plugin.kotlinGradle)
}

gradlePlugin {
  plugins {
    create("appyx-publish-android") {
      id = "appyx-publish-android"
      implementationClass = "AndroidAppyxPublishPlugin"
    }
    create("appyx-publish-java") {
      id = "appyx-publish-java"
      implementationClass = "JavaAppyxPublishPlugin"
    }
  }
}

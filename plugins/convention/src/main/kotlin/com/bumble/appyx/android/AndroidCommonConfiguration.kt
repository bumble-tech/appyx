package com.bumble.appyx.android

import org.gradle.api.Project

internal fun Project.applyAndroidPlugin(
    androidPluginId: String,
) {
    extensions.create("appyx", AndroidConventionExtension::class.java)

    plugins.apply(androidPluginId)
    plugins.apply("kotlin-android")
    plugins.apply("appyx-lint")
    plugins.apply("appyx-detekt")
}

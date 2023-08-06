package com.bumble.appyx.android.library

import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.bumble.appyx.android.applyAndroidPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project
            .applyAndroidPlugin<LibraryDefaultConfig, LibraryExtension, LibraryAndroidComponentsExtension>(
                "com.android.library"
            ) { extension, libs ->
                defaultConfig {
                    targetSdk = libs.findVersion("androidTargetSdk").get().displayName.toInt()
                }
            }
    }
}

package com.bumble.appyx.android.application

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.bumble.appyx.android.applyAndroidPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project
            .applyAndroidPlugin<ApplicationDefaultConfig, ApplicationExtension, ApplicationAndroidComponentsExtension>(
                "com.android.application"
            ) { extension, libs ->
                defaultConfig {
                    applicationId = extension.namespace.get()
                    targetSdk = libs.findVersion("androidTargetSdk").get().displayName.toInt()
                    versionCode = 1
                    versionName = "1.0"
                }
            }
    }
}

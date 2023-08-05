package com.bumble.appyx.android.application

import com.bumble.appyx.android.applyAndroidPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project
            .applyAndroidPlugin(
                "com.android.application"
            )
    }
}

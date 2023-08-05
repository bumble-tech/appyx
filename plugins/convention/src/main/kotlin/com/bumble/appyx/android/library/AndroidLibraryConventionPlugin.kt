package com.bumble.appyx.android.library

import com.bumble.appyx.android.applyAndroidPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project
            .applyAndroidPlugin(
                "com.android.library"
            )
    }
}

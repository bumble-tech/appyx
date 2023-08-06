package com.bumble.appyx.android

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.variant.AndroidComponentsExtension
import com.bumble.appyx.versionCatalog
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal inline fun <
        reified ConfigT : DefaultConfig,
        reified DslExtensionT : CommonExtension<*, *, ConfigT, *>,
        reified ComponentsExtensionT : AndroidComponentsExtension<DslExtensionT, *, *>
        > Project.applyAndroidPlugin(
    androidPluginId: String,
    noinline onExtension: DslExtensionT.(AndroidConventionExtension, VersionCatalog) -> Unit = { _, _ -> },
) {
    val appyxExtension = extensions.create("appyx", AndroidConventionExtension::class.java)
    appyxExtension.initDefaults()

    plugins.apply(androidPluginId)
    plugins.apply("kotlin-android")
    plugins.apply("appyx-lint")
    plugins.apply("appyx-detekt")

    val libs = project.versionCatalog

    dependencies.add("testImplementation", libs.findLibrary("junit-api").get())
    dependencies.add("testRuntimeOnly", libs.findLibrary("junit-engine").get())

    extensions
        .getByType<ComponentsExtensionT>()
        .finalizeDsl { extension ->
            extension.configure(
                appyxExtension = appyxExtension,
                libs = libs,
            )
            onExtension(extension, appyxExtension, libs)

            if (appyxExtension.buildFeatures.kotlinParcelize.get()) {
                plugins.apply("kotlin-parcelize")
            }
        }
}

private fun <DefaultConfigT : DefaultConfig> CommonExtension<*, *, DefaultConfigT, *>.configure(
    appyxExtension: AndroidConventionExtension,
    libs: VersionCatalog,
) {
    namespace = appyxExtension.namespace.get()
    compileSdk = libs.findVersion("androidCompileSdk").get().displayName.toInt()

    defaultConfig {
        minSdk = libs.findVersion("androidMinSdk").get().displayName.toInt()

        vectorDrawables {
            useSupportLibrary = true
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val composeEnabled = appyxExtension.buildFeatures.compose.get()
    buildFeatures {
        compose = composeEnabled
    }
    if (composeEnabled) {
        composeOptions {
            kotlinCompilerExtensionVersion =
                libs.findVersion("composeCompiler").get().displayName
        }
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    packagingOptions {
        resources.excludes.apply {
            add("META-INF/LICENSE.md")
            add("META-INF/LICENSE-notice.md")
        }
    }
}

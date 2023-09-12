package com.bumble.appyx.android

import com.bumble.appyx.BaseConventionExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

abstract class AndroidConventionExtension : BaseConventionExtension {
    abstract val namespace: Property<String>

    @get:Nested
    abstract val buildFeatures: BuildFeatures

    fun buildFeatures(action: BuildFeatures.() -> Unit) {
        action(buildFeatures)
    }

    fun initDefaults() {
        buildFeatures.initDefaults()
    }

    abstract class BuildFeatures {
        abstract val compose: Property<Boolean>
        abstract val kotlinParcelize: Property<Boolean>

        fun initDefaults() {
            compose.convention(false)
            kotlinParcelize.convention(false)
        }
    }
}

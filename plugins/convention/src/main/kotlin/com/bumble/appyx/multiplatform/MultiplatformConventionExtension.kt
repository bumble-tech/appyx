package com.bumble.appyx.multiplatform

import com.bumble.appyx.BaseConventionExtension
import org.gradle.api.provider.Property

abstract class MultiplatformConventionExtension : BaseConventionExtension {
    abstract val androidNamespace: Property<String>
}

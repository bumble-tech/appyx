package com.bumble.appyx.dagger.hilt

import dagger.hilt.GeneratesRootInput
import kotlin.reflect.KClass

/**
 * Can be used to annotate an Appyx Node Factory that does not need any external dependencies.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@GeneratesRootInput
annotation class HiltNodeFactory(
    val nodeClass: KClass<*>
)

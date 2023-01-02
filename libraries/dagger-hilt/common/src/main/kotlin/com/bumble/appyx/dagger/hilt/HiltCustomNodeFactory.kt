package com.bumble.appyx.dagger.hilt

import dagger.hilt.GeneratesRootInput
import kotlin.reflect.KClass

/**
 * Specifies that the hilt appyx compiler should generate a module and make the factory
 * accessible via the [NodeFactoryProvider].
 *
 * If you want to specify a different node key, specify this using factoryClass.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@GeneratesRootInput
annotation class HiltCustomNodeFactory(
    val factoryClass: KClass<*> = Any::class
)

package com.bumble.appyx.dagger.hilt

import dagger.hilt.GeneratesRootInput
import kotlin.reflect.KClass

/**
 * Can be used to annotate an Appyx Node that does not have an arguments other than BuildContext
 * This will then generate a factory which can provide this anywhere without exposing the actual
 * node implementation.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@GeneratesRootInput
annotation class HiltNode(
    val nodeClass: KClass<*> = Any::class
)

package com.bumble.appyx.dagger.hilt.internal

import javax.inject.Qualifier

/**
 * This class cannot be internal (as the generated kotlin classes need to reference it)
 * It is not intended to be used outside the library.
 */
@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
)
annotation class CustomNodeFactoryQualifier

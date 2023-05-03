package com.bumble.appyx.dagger.hilt

import dagger.hilt.GeneratesRootInput

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@GeneratesRootInput
annotation class HiltAggregateNodeFactory

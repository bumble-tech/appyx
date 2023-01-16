package com.bumble.appyx.interactions

expect fun getPlatformName(): String

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
expect annotation class RawValue()

expect interface Parcelable

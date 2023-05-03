package com.bumble.appyx.dagger.hilt.generator

import com.squareup.kotlinpoet.ClassName

const val APPYX_HILT_PACKAGE = "com.bumble.appyx.dagger.hilt"
const val APPYX_HILT_INTERNAL_PACKAGE = "$APPYX_HILT_PACKAGE.internal"

val nodeFactoryClassName = ClassName("com.bumble.appyx.core.integration", "NodeFactory")

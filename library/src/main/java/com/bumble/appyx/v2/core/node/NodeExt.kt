package com.bumble.appyx.v2.core.node

fun <T : Node> T.build(): T = also { it.onBuilt() }

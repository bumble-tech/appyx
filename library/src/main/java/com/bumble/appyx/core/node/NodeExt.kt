package com.bumble.appyx.core.node

fun <T : Node> T.build(): T = also { it.onBuilt() }

package com.bumble.appyx.navigation.node

fun <T : AbstractNode> T.build(): T = also { it.onBuilt() }

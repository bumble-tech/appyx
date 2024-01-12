package com.bumble.appyx.navigation.node

fun <T : Node<*>> T.build(): T = also { it.onBuilt() }

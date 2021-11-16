package com.github.zsoltk.composeribs.core.node

fun <T : Node> T.build(): T = also { it.onBuilt() }

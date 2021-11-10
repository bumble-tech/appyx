package com.github.zsoltk.composeribs.core

fun <T : Node> T.build(): T = also { it.onBuilt() }

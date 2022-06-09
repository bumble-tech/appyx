package com.bumble.appyx.v2.core.node

fun interface ViewFactory<out View : NodeView> : () -> View

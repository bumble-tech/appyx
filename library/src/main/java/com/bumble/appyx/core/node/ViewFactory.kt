package com.bumble.appyx.core.node

fun interface ViewFactory<out View : NodeView> : () -> View

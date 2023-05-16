package com.bumble.appyx.navigation.node

fun interface ViewFactory<out View : NodeView> : () -> View

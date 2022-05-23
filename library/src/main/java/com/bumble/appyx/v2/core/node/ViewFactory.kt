package com.bumble.appyx.v2.core.node

fun interface ViewFactory<N : Node> : () -> AbstractNodeView<N>

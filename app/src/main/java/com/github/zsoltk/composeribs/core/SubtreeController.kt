package com.github.zsoltk.composeribs.core

data class SubtreeController<T>(
    val backStack: BackStack<T>,
    val resolver: Resolver<T>
)

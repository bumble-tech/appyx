package com.github.zsoltk.composeribs.core.routing

import com.github.zsoltk.composeribs.core.Node

interface Resolver<T> {

    fun resolve(routing: T) : Node<*>
}

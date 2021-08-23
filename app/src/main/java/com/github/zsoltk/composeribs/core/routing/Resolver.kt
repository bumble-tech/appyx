package com.github.zsoltk.composeribs.core.routing

import com.github.zsoltk.composeribs.core.Node

typealias Resolver<T> = (T) -> Node<*>

package com.github.zsoltk.composeribs.core.routing

import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.SavedStateMap

interface Resolver<T> {
    fun resolve(routing: T, savedStateMap: SavedStateMap?): Node<*>
}

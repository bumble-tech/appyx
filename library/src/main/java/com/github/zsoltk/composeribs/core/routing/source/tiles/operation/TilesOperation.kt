package com.github.zsoltk.composeribs.core.routing.source.tiles.operation

import com.github.zsoltk.composeribs.core.routing.Operation
import com.github.zsoltk.composeribs.core.routing.source.tiles.Tiles

sealed interface TilesOperation<T> : Operation<T, Tiles.TransitionState>

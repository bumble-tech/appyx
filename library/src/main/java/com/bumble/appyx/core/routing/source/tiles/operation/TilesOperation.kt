package com.bumble.appyx.core.routing.source.tiles.operation

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.core.routing.source.tiles.Tiles

sealed interface TilesOperation<T> : Operation<T, Tiles.TransitionState>

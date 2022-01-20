package com.bumble.appyx.v2.core.routing.source.tiles.operation

import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.source.tiles.Tiles

sealed interface TilesOperation<T> : Operation<T, Tiles.TransitionState>

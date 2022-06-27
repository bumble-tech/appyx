package com.bumble.appyx.routingsource.tiles.operation

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.routingsource.tiles.Tiles

sealed interface TilesOperation<T> : Operation<T, Tiles.TransitionState>

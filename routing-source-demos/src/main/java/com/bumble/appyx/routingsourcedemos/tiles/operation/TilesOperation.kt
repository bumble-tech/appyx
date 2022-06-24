package com.bumble.appyx.routingsourcedemos.tiles.operation

import com.bumble.appyx.core.routing.Operation
import com.bumble.appyx.routingsourcedemos.tiles.Tiles

sealed interface TilesOperation<T> : Operation<T, Tiles.TransitionState>

package com.bumble.appyx.navmodel.tiles.operation

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.tiles.Tiles

interface TilesOperation<T> : Operation<T, Tiles.State>

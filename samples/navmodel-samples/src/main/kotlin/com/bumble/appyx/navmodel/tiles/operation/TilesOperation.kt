package com.bumble.appyx.navmodel.tiles.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.tiles.Tiles

interface TilesOperation<T : Parcelable> : Operation<T, Tiles.State>

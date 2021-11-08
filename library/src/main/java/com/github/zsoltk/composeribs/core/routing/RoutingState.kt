package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoutingState<Key, State>(
    val elements: RoutingElements<Key, State>,
    val operation: Operation<Key, State>
) : Parcelable

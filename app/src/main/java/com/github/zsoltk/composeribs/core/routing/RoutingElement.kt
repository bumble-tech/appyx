package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoutingElement<Key : Parcelable, State : Parcelable>(
    val key: RoutingKey<Key>,
    val fromState: State,
    val targetState: State,
    // TODO Should be calculated from targetState
    val onScreen: Boolean,
) : Parcelable

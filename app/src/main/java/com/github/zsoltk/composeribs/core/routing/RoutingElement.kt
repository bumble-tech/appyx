package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class RoutingElement<Key, State>(
    val key: @RawValue RoutingKey<Key>,
    val fromState: @RawValue State,
    val targetState: @RawValue State,
    // TODO Should be calculated from targetState
    val onScreen: Boolean,
) : Parcelable

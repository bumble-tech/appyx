package com.github.zsoltk.composeribs.core.routing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class RoutingElement<Key, State>(
    private val onScreenResolver: OnScreenResolver<State>,
    val key: @RawValue RoutingKey<Key>,
    val fromState: @RawValue State,
    val targetState: @RawValue State,
    val onScreen: Boolean
) : Parcelable {

    fun transitionTo(targetState: @RawValue State): RoutingElement<Key, State> =
        RoutingElement(
            onScreenResolver = onScreenResolver,
            key = key,
            fromState = fromState,
            targetState = targetState,
            onScreen = this.onScreen || onScreenResolver.resolve(targetState)
        )

    fun transitionFinished(targetState: @RawValue State): RoutingElement<Key, State> =
        RoutingElement(
            onScreenResolver = onScreenResolver,
            key = key,
            fromState = this.targetState,
            targetState = this.targetState,
            onScreen = onScreenResolver.resolve(targetState)
        )

}

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
    val isOnScreen: @RawValue Boolean,
    val operation: @RawValue Operation<Key, out State>
) : Parcelable {

    fun transitionTo(
        targetState: @RawValue State,
        operation: @RawValue Operation<Key, out State>
    ): RoutingElement<Key, State> =
        RoutingElement(
            onScreenResolver = onScreenResolver,
            key = key,
            fromState = fromState,
            targetState = targetState,
            isOnScreen = this.isOnScreen || onScreenResolver.isOnScreen(targetState),
            operation = operation
        )

    fun transitionFinished(
        targetState: @RawValue State
    ): RoutingElement<Key, State> =
        RoutingElement(
            onScreenResolver = onScreenResolver,
            key = key,
            fromState = this.targetState,
            targetState = this.targetState,
            isOnScreen = onScreenResolver.isOnScreen(targetState),
            operation = operation
        )

}

package com.bumble.appyx.v2.app.node.teaser.promoter.routingsource

import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingElements
import com.bumble.appyx.v2.core.routing.RoutingKey

class Promoter<T : Any>(
    initialElements: List<T> = listOf(),
) : BaseRoutingSource<T, Promoter.TransitionState>(
    screenResolver = PromoterOnScreenResolver
) {
    enum class TransitionState {
        CREATED, STAGE1, STAGE2, STAGE3, STAGE4, SELECTED, DESTROYED;

        fun next(): TransitionState =
            when (this) {
                CREATED -> STAGE1
                STAGE1 -> STAGE2
                STAGE2 -> STAGE3
                STAGE3 -> STAGE4
                STAGE4 -> SELECTED
                SELECTED -> DESTROYED
                DESTROYED -> DESTROYED
            }
    }

    override val initialState: RoutingElements<T, TransitionState> =
        initialElements.map {
            PromoterElement(
                key = RoutingKey(it),
                fromState = TransitionState.CREATED,
                targetState = TransitionState.STAGE1,
                operation = Operation.Noop()
            )
        }

    override fun onTransitionFinished(key: RoutingKey<T>) {
        updateState { list ->
            list.mapNotNull {
                if (it.key == key) {
                    if (it.targetState == TransitionState.DESTROYED) {
                        null
                    } else {
                        it.onTransitionFinished()
                    }
                } else {
                    it
                }
            }
        }
    }
}

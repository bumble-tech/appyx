package com.bumble.appyx.v2.app.node.teaser.promoter.routingsource

import com.bumble.appyx.v2.app.node.teaser.promoter.routingsource.Promoter.TransitionState.DESTROYED
import com.bumble.appyx.v2.core.routing.BaseRoutingSource
import com.bumble.appyx.v2.core.routing.Operation
import com.bumble.appyx.v2.core.routing.RoutingKey

class Promoter<T : Any>(
    initialRoutings: List<T> = listOf(),
) : BaseRoutingSource<T, Promoter.TransitionState>(
    screenResolver = PromoterOnScreenResolver,
    finalState = DESTROYED
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

    override val initialElements = initialRoutings.map {
        PromoterElement(
            key = RoutingKey(it),
            fromState = TransitionState.CREATED,
            targetState = TransitionState.STAGE1,
            operation = Operation.Noop()
        )
    }
}

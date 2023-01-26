package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.cards.CardsModel

@Parcelize
class VoteLike<NavTarget>(
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
): TopCardOperation<NavTarget>() {

    override fun createTargetState(fromState: CardsModel.State<NavTarget>): CardsModel.State<NavTarget> =
        fromState.copy(
            liked = fromState.liked + fromState.queued.first(),
            queued = fromState.queued.drop(1)
        )
}

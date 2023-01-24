package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.transitionmodel.cards.CardsModel

@Parcelize
class VotePass<NavTarget> : TopCardOperation<NavTarget>() {

    override fun createTargetState(fromState: CardsModel.State<NavTarget>): CardsModel.State<NavTarget> =
        fromState.copy(
            passed = fromState.passed + fromState.queued.first(),
            queued = fromState.queued.drop(1)
        )
}

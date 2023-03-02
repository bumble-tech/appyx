package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.transitionmodel.cards.CardsModel

abstract class TopCardOperation<NavTarget> : BaseOperation<CardsModel.State<NavTarget>>() {

    override fun createFromState(baseLineState: CardsModel.State<NavTarget>): CardsModel.State<NavTarget> =
        baseLineState

    override fun isApplicable(state: CardsModel.State<NavTarget>): Boolean =
        state.visibleCards.isNotEmpty()
}

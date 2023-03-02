package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.transitionmodel.cards.CardsModel

abstract class TopCardOperation<InteractionTarget> : BaseOperation<CardsModel.State<InteractionTarget>>() {

    override fun createFromState(baseLineState: CardsModel.State<InteractionTarget>): CardsModel.State<InteractionTarget> =
        baseLineState

    override fun isApplicable(state: CardsModel.State<InteractionTarget>): Boolean =
        state.visibleCards.isNotEmpty()
}

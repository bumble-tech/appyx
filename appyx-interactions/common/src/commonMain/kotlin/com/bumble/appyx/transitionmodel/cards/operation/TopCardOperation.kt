package com.bumble.appyx.transitionmodel.cards.operation

//abstract class TopCardOperation<NavTarget>(
//    private val newTargetState: CardsModel.State
//) : CardsOperation<NavTarget> {
//
//    override fun isApplicable(elements: CardsElements<NavTarget>): Boolean =
//        elements.any { it.state in CardsModel.TOP_STATES }
//
//    override fun invoke(elements: CardsElements<NavTarget>): NavTransition<NavTarget, CardsModel.State> {
//        val targetIndex = elements.indexOfFirst { it.state in CardsModel.TOP_STATES }
//
//        return NavTransition(
//            fromState = elements,
//            targetState = elements.mapIndexed { index, element ->
//                if (index == targetIndex) {
//                    element.transitionTo(newTargetState = newTargetState, this)
//                } else {
//                    element.transitionTo(newTargetState = element.state.next(), this)
//                }
//            }
//        )
//    }
//}

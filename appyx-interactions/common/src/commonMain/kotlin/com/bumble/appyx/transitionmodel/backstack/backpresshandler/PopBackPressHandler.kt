package com.bumble.appyx.transitionmodel.backstack.backpresshandler

//class PopBackPressHandler<NavTarget : Any> :
//    BaseBackPressHandlerStrategy<NavTarget, BackStackModel.State<NavTarget>>() {
//
//    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
//        navModel.segments.map {
//            areThereStashedElements(
//                it.navTransition.targetState
//            )
//        }
//    }
//
//    private fun areThereStashedElements(elements: NavElements<NavTarget, State>): Boolean =
//        elements.any { it.state == State.STASHED }
//
//    override fun onBackPressed() {
//        // FIXME this would need to talk to an InputSource instead to get a transition too
//        navModel.enqueue(Pop())
//    }
//}

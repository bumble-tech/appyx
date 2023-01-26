package com.bumble.appyx.transitionmodel.backstack.interpolator

import com.bumble.appyx.interactions.core.TransitionModel
import com.bumble.appyx.interactions.core.ui.ScreenState
import com.bumble.appyx.interactions.core.ui.VisibilityInterpolator
import com.bumble.appyx.transitionmodel.backstack.BackStackModel

class BackStackVisibilityInterpolator<NavTarget> : VisibilityInterpolator<NavTarget, BackStackModel.State<NavTarget>> {

    override fun mapVisibility(segment: TransitionModel.Segment<BackStackModel.State<NavTarget>>): ScreenState<NavTarget> {
        val fromState = segment.navTransition.fromState
        val targetState = segment.navTransition.targetState

        val onScreenElements = (listOf(fromState.active) + targetState.active).toSet()

        val allElements =
            (listOf(targetState.active) + targetState.created + targetState.stashed + targetState.destroyed).toSet()

        return ScreenState(
            onScreen = onScreenElements,
            offScreen = allElements - onScreenElements
        )
    }
}

package com.bumble.appyx.transitionmodel.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.NavElements
import com.bumble.appyx.interactions.core.NavTransition
import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.interactions.core.inputsource.AnimatedInputSource
import com.bumble.appyx.interactions.core.inputsource.InputSource
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.transitionmodel.spotlight.Spotlight.State.INACTIVE_BEFORE


@Parcelize
class Previous<NavTarget> : Operation<NavTarget, State> {

    override fun isApplicable(elements: NavElements<NavTarget, State>) =
        elements.any { it.state == INACTIVE_BEFORE }

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        val previousKey = elements.last { it.state == INACTIVE_BEFORE }.key

        val targetState = elements.map {
            when {
                it.state == ACTIVE -> {
                    it.transitionTo(
                        newTargetState = INACTIVE_AFTER,
                        operation = this
                    )
                }
                it.key == previousKey -> {
                    it.transitionTo(
                        newTargetState = ACTIVE,
                        operation = this
                    )
                }
                else -> {
                    it
                }
            }
        }

        return NavTransition(
            fromState = elements,
            targetState = targetState
        )
    }
}

fun <NavTarget : Any> InputSource<NavTarget, State>.previous() {
    operation(Previous())
}

fun <NavTarget : Any> AnimatedInputSource<NavTarget, State>.previous(animationSpec: AnimationSpec<Float>) {
    operation(Previous(), animationSpec)
}



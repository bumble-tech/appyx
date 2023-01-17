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
class Next<NavTarget> : Operation<NavTarget, State> {

    override fun isApplicable(elements: NavElements<NavTarget, State>) =
        elements.any { it.fromState == INACTIVE_AFTER && it.state == INACTIVE_AFTER }

    override fun invoke(elements: NavElements<NavTarget, State>): NavTransition<NavTarget, State> {
        val nextKey = elements.first { it.state == INACTIVE_AFTER }.key

        val targetState = elements.map {
            when {
                it.state == ACTIVE -> {
                    it.transitionTo(
                        newTargetState = INACTIVE_BEFORE,
                        operation = this
                    )
                }
                it.key == nextKey -> {
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

fun <NavTarget : Any> InputSource<NavTarget, State>.next() {
    operation(Next())
}

fun <NavTarget : Any> AnimatedInputSource<NavTarget, State>.next(animationSpec: AnimationSpec<Float>) {
    operation(Next(), animationSpec)
}


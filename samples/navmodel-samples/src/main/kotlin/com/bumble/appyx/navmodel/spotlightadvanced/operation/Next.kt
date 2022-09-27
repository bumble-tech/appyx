package com.bumble.appyx.navmodel.spotlightadvanced.operation

import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Active
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Carousel
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.InactiveAfter
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.InactiveBefore
import kotlinx.parcelize.Parcelize

@Parcelize
class Next<T : Any> : SpotlightAdvancedOperation<T> {

    override fun isApplicable(elements: NavElements<T, State>) =
        elements.any { (it.fromState == InactiveAfter && it.targetState == InactiveAfter) || it.fromState is Carousel }

    override fun invoke(elements: NavElements<T, State>): NavElements<T, State> {
        if (elements.all { it.fromState is Carousel }) {
            return elements.map {
                when (val state = it.fromState) {
                    is Carousel -> {
                        val currentOffset = (it.fromState as Carousel).offset
                        it.transitionTo(
                            newTargetState = state.copy(offset = currentOffset + 1),
                            operation = this
                        )
                    }
                    else -> {
                        it
                    }
                }

            }
        } else {
            val nextKey =
                elements.first { it.targetState == InactiveAfter }.key

            return elements.map {
                when {
                    it.targetState == Active -> {
                        it.transitionTo(
                            newTargetState = InactiveBefore,
                            operation = this
                        )
                    }
                    it.key == nextKey -> {
                        it.transitionTo(
                            newTargetState = Active,
                            operation = this
                        )
                    }
                    else -> {
                        it
                    }
                }
            }
        }


    }
}

fun <T : Any> SpotlightAdvanced<T>.next() {
    accept(Next())
}


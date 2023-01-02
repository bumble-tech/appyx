package com.bumble.appyx.navmodel.spotlightadvanced.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Active
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.Carousel
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.InactiveAfter
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State.InactiveBefore
import kotlinx.parcelize.Parcelize


@Parcelize
class Previous<T : Parcelable> : SpotlightAdvancedOperation<T> {

    override fun isApplicable(elements: NavElements<T, State>) =
        elements.any {
            (it.fromState == InactiveBefore && it.targetState == InactiveBefore) ||
                    it.fromState is Carousel
        }

    private fun transformCarousel(elements: NavElements<T, State>): NavElements<T, State> {
        return elements.map {
            when (val state = it.fromState) {
                is Carousel -> {
                    val currentOffset = (it.fromState as Carousel).offset
                    val newOffset = currentOffset - 1
                    it.transitionTo(
                        newTargetState = state.copy(offset = newOffset),
                        operation = this
                    )
                }
                else -> {
                    it
                }
            }
        }
    }


    private fun transformPager(elements: NavElements<T, State>): NavElements<T, State> {
        val previousKey =
            elements.last { it.targetState == InactiveBefore }.key

        return elements.map {
            when {
                it.targetState == Active -> {
                    it.transitionTo(
                        newTargetState = InactiveAfter,
                        operation = this
                    )
                }
                it.key == previousKey -> {
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

    override fun invoke(elements: NavElements<T, State>): NavElements<T, State> {
        return if (elements.all { it.fromState is Carousel }) {
            transformCarousel(elements)
        } else {
            transformPager(elements)
        }
    }
}

fun <T : Parcelable> SpotlightAdvanced<T>.previous() {
    accept(Previous())
}

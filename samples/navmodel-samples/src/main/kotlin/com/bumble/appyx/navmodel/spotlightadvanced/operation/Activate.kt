package com.bumble.appyx.navmodel.spotlightadvanced.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced
import com.bumble.appyx.navmodel.spotlightadvanced.SpotlightAdvanced.State
import com.bumble.appyx.navmodel.spotlightadvanced.currentIndex
import kotlinx.parcelize.Parcelize

@Parcelize
class Activate<T : Parcelable>(
    private val index: Int
) : SpotlightAdvancedOperation<T> {

    override fun isApplicable(elements: NavElements<T, State>) =
        index != elements.currentIndex && index <= elements.lastIndex && index >= 0

    override fun invoke(elements: NavElements<T, State>): NavElements<T, State> {

        val toActivateIndex = this.index
        return elements.mapIndexed { index, element ->
            when {
                index < toActivateIndex -> {
                    element.transitionTo(
                        newTargetState = State.InactiveBefore,
                        operation = this
                    )
                }
                index == toActivateIndex -> {
                    element.transitionTo(
                        newTargetState = State.Active,
                        operation = this
                    )
                }
                else -> {
                    element.transitionTo(
                        newTargetState = State.InactiveAfter,
                        operation = this
                    )
                }
            }
        }
    }
}

fun <T : Parcelable> SpotlightAdvanced<T>.activate(index: Int) {
    accept(Activate(index))
}

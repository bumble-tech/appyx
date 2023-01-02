package com.bumble.appyx.navmodel.spotlight.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.Spotlight.State
import com.bumble.appyx.navmodel.spotlight.Spotlight.State.ACTIVE
import com.bumble.appyx.navmodel.spotlight.Spotlight.State.INACTIVE_AFTER
import com.bumble.appyx.navmodel.spotlight.Spotlight.State.INACTIVE_BEFORE
import kotlinx.parcelize.Parcelize

@Parcelize
class Next<T : Parcelable> : SpotlightOperation<T> {

    override fun isApplicable(elements: NavElements<T, State>) =
        elements.any { it.fromState == INACTIVE_AFTER && it.targetState == INACTIVE_AFTER }

    override fun invoke(elements: NavElements<T, State>): NavElements<T, State> {
        val nextKey =
            elements.first { it.targetState == INACTIVE_AFTER }.key

        return elements.map {
            when {
                it.targetState == ACTIVE -> {
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
    }
}

fun <T : Parcelable> Spotlight<T>.next() {
    accept(Next())
}


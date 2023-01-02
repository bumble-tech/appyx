package com.bumble.appyx.navmodel.backstack.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.activeIndex
import kotlinx.parcelize.Parcelize

/**
 * Operation:
 *
 * [A, B, C] + Remove(id of B) = [A, C]
 */
@Parcelize
data class Remove<T : Parcelable>(
    private val key: NavKey<T>
) : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>) =
        elements.hasContentWithKey()

    override fun invoke(
        elements: BackStackElements<T>
    ): BackStackElements<T> =
        when {
            elements.hasContentWithKey() -> updateContent(elements)
            else -> elements
        }

    private fun BackStackElements<T>.hasContentWithKey() =
        any { it.key == key && it.targetState != BackStack.State.DESTROYED }

    private fun updateContent(elements: BackStackElements<T>): BackStackElements<T> {
        val toRemove =
            elements.find { it.key == key && it.targetState != BackStack.State.DESTROYED }

        requireNotNull(toRemove)
        val toRemoveIndex = elements.indexOf(toRemove)

        return if (toRemoveIndex == elements.activeIndex) {
            val unStashIndex =
                elements.indexOfLast { it.targetState == BackStack.State.STASHED }
            require(unStashIndex != -1) { "Nothing to remove from stash, state=$elements" }

            elements.mapIndexed { index, element ->
                when (index) {
                    toRemoveIndex -> element.transitionTo(
                        newTargetState = BackStack.State.DESTROYED,
                        operation = this
                    )
                    unStashIndex -> element.transitionTo(
                        newTargetState = BackStack.State.ACTIVE,
                        operation = this
                    )
                    else -> element
                }
            }
        } else {
            elements.minus(toRemove)
        }
    }
}

fun <T : Parcelable> BackStack<T>.remove(key: NavKey<T>) {
    accept(Remove(key))
}

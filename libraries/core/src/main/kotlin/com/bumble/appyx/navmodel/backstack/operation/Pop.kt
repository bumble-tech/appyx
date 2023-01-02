package com.bumble.appyx.navmodel.backstack.operation

import android.os.Parcelable
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElements
import com.bumble.appyx.navmodel.backstack.activeIndex
import kotlinx.parcelize.Parcelize

/**
 * Operation:
 *
 * [A, B, C] + Pop = [A, B]
 */
@Parcelize
class Pop<T : Parcelable> : BackStackOperation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean =
        elements.any { it.targetState == BackStack.State.ACTIVE } &&
                elements.any { it.targetState == BackStack.State.STASHED }

    override fun invoke(
        elements: BackStackElements<T>
    ): BackStackElements<T> {

        val destroyIndex = elements.activeIndex
        val unStashIndex =
            elements.indexOfLast { it.targetState == BackStack.State.STASHED }
        require(destroyIndex != -1) { "Nothing to destroy, state=$elements" }
        require(unStashIndex != -1) { "Nothing to remove from stash, state=$elements" }
        return elements.mapIndexed { index, element ->
            when (index) {
                destroyIndex -> element.transitionTo(
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
    }

    override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

    override fun hashCode(): Int = this.javaClass.hashCode()
}

fun <T : Parcelable> BackStack<T>.pop() {
    accept(Pop())
}

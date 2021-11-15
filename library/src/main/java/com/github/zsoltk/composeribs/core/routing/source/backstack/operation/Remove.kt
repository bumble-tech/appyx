package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElements
import com.github.zsoltk.composeribs.core.routing.source.backstack.currentIndex

/**
 * Operation:
 *
 * [A, B, C] + Remove(id of B) = [A, C]
 */
internal class Remove<T : Any>(
    private val key: RoutingKey<T>
) : BackStack.Operation<T> {

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
        find { it.key == key && it.targetState != BackStack.TransitionState.DESTROYED } != null

    private fun updateContent(elements: BackStackElements<T>): BackStackElements<T> {
        val toRemove =
            elements.find { it.key == key && it.targetState != BackStack.TransitionState.DESTROYED }

        requireNotNull(toRemove)
        val toRemoveIndex = elements.indexOf(toRemove)

        return if (toRemoveIndex == elements.currentIndex) {
            val unStashIndex =
                elements.indexOfLast { it.targetState == BackStack.TransitionState.STASHED_IN_BACK_STACK }
            require(unStashIndex != -1) { "Nothing to remove from stash, state=$elements" }

            elements.mapIndexed { index, element ->
                when (index) {
                    toRemoveIndex -> element.copy(targetState = BackStack.TransitionState.DESTROYED)
                    unStashIndex -> element.copy(targetState = BackStack.TransitionState.ON_SCREEN)
                    else -> element
                }
            }
        } else {
            elements.minus(toRemove)
        }
    }
}

fun <T : Any> BackStack<T>.remove(key: RoutingKey<T>) {
    perform(Remove(key))
}

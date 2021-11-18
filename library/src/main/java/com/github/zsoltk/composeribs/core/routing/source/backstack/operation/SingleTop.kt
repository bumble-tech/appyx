package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.OnScreenResolver
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElements
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackOperation
import com.github.zsoltk.composeribs.core.routing.source.backstack.current

/**
 * Operation:
 *
 * [A, B, C, D] + SingleTop(B) = [A, B]          // of same type and equals, acts as n * Pop
 * [A, B, C, D] + SingleTop(B') = [A, B']        // of same type but not equals, acts as n * Pop + Replace
 * [A, B, C, D] + SingleTop(E) = [A, B, C, D, E] // not found, acts as Push
 */
sealed class SingleTop<T : Any> : BackStackOperation<T> {

    class SingleTopReactivateBackStackOperation<T : Any>(
        private val element: T,
        private val position: Int
    ) : SingleTop<T>() {

        override fun isApplicable(elements: BackStackElements<T>): Boolean =
            element != elements.current?.key?.routing

        override fun invoke(
            elements: BackStackElements<T>
        ): BackStackElements<T> {
            val current = elements.current
            requireNotNull(current)

            val newElements = elements.dropLast(elements.size - position - 1)

            return newElements.mapIndexed { index, element ->
                if (index == newElements.lastIndex) {
                    element.transitionTo(
                        targetState = BackStack.TransitionState.ON_SCREEN,
                        operation = this
                    )
                } else {
                    element
                }
            } + current.transitionTo(
                targetState = BackStack.TransitionState.DESTROYED,
                operation = this
            )
        }

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }

    class SingleTopReplaceBackStackOperation<T : Any>(
        private val element: T,
        private val position: Int,
    ) : SingleTop<T>() {

        override fun isApplicable(elements: BackStackElements<T>): Boolean = true

        override fun invoke(
            elements: BackStackElements<T>
        ): BackStackElements<T> {
            val current = elements.current
            requireNotNull(current)

            val newElements = elements.dropLast(elements.size - position)

            return newElements + current.transitionTo(
                targetState = BackStack.TransitionState.DESTROYED,
                operation = this
            ) + BackStackElement(
                key = RoutingKey(element),
                fromState = BackStack.TransitionState.CREATED,
                targetState = BackStack.TransitionState.ON_SCREEN,
                operation = this,
            )
        }

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }

    companion object {

        fun <T : Any> init(
            element: T,
            elements: BackStackElements<T>
        ): BackStackOperation<T> {
            val targetClass = element.javaClass
            val lastIndexOfSameClass =
                elements.indexOfLast { targetClass.isInstance(it.key.routing) }

            return if (lastIndexOfSameClass == -1) {
                Push(element)
            } else {
                if (elements[lastIndexOfSameClass].key.routing == element) {
                    SingleTopReactivateBackStackOperation(
                        element,
                        lastIndexOfSameClass
                    )
                } else {
                    SingleTopReplaceBackStackOperation(
                        element,
                        lastIndexOfSameClass
                    )
                }
            }
        }
    }
}

fun <T : Any> BackStack<T>.singleTop(element: T) {
    perform(SingleTop.init(element, elements.value))
}

package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.*

/**
 * Operation:
 *
 * [A, B, C, D] + SingleTop(B) = [A, B]          // of same type and equals, acts as n * Pop
 * [A, B, C, D] + SingleTop(B') = [A, B']        // of same type but not equals, acts as n * Pop + Replace
 * [A, B, C, D] + SingleTop(E) = [A, B, C, D, E] // not found, acts as Push
 */
internal class SingleTop<T : Any>(
    private val element: T
) : BackStack.Operation<T> {

    override fun isApplicable(elements: BackStackElements<T>): Boolean = true

    override fun invoke(
        elements: BackStackElements<T>,
        uuidGenerator: UuidGenerator
    ): BackStackElements<T> {
        val targetClass = element.javaClass
        val lastIndexOfSameClass = elements.indexOfLast { targetClass.isInstance(it.key.routing) }

        val operation: (BackStackElements<T>, UuidGenerator) -> BackStackElements<T> =
            if (lastIndexOfSameClass == -1) {
                Push(element)
            } else {
                if (elements[lastIndexOfSameClass].key.routing == element) {
                    SingleTopReactivateBackStackOperation(lastIndexOfSameClass)
                } else {
                    SingleTopReplaceBackStackOperation(element, lastIndexOfSameClass)
                }
            }

        return operation.invoke(elements, uuidGenerator)
    }

    private class SingleTopReactivateBackStackOperation<T : Any>(
        private val position: Int
    ) : (BackStackElements<T>, UuidGenerator) -> BackStackElements<T> {

        override fun invoke(
            elements: BackStackElements<T>,
            uuidGenerator: UuidGenerator
        ): BackStackElements<T> {
            val current = elements.current
            requireNotNull(current)

            val newElements = elements.dropLast(elements.size - position - 1)

            return newElements.mapIndexed { index, element ->
                if (index == newElements.lastIndex) {
                    element.copy(targetState = BackStack.TransitionState.ON_SCREEN)
                } else {
                    element
                }
            } + current.copy(targetState = BackStack.TransitionState.DESTROYED)
        }
    }

    private class SingleTopReplaceBackStackOperation<T : Any>(
        private val element: T,
        private val position: Int
    ) : (BackStackElements<T>, UuidGenerator) -> BackStackElements<T> {

        override fun invoke(
            elements: BackStackElements<T>,
            uuidGenerator: UuidGenerator
        ): BackStackElements<T> {
            val current = elements.current
            requireNotNull(current)

            val newElements = elements.dropLast(elements.size - position)

            return newElements + current.copy(targetState = BackStack.TransitionState.DESTROYED) + BackStackElement(
                key = BackStack.LocalRoutingKey(element, uuidGenerator.incrementAndGet()),
                fromState = BackStack.TransitionState.CREATED,
                targetState = BackStack.TransitionState.ON_SCREEN,
            )
        }
    }
}

fun <T : Any> BackStack<T>.singleTop(element: T) {
    perform(SingleTop(element))
}

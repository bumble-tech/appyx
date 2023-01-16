package com.bumble.appyx.interactions.core


interface Operation<NavTarget, State> :
        (NavElements<NavTarget, State>) -> NavTransition<NavTarget, State> {
    // FIXME, Parcelable {

    fun isApplicable(elements: NavElements<NavTarget, State>): Boolean

    /**
     * Transitions all elements to a new state
     */
    fun NavElements<NavTarget, State>.transitionTo(
        newTargetState: State
    ) = map { element ->
        element.transitionTo(
            newTargetState = newTargetState,
            operation = this@Operation
        )
    }

    /**
     * Transitions all elements to a new state based on the individual elements
     */
    fun NavElements<NavTarget, State>.transitionTo(
        newTargetState: (NavElement<NavTarget, State>) -> State
    ) = map { element ->
        element.transitionTo(
            newTargetState = newTargetState.invoke(element),
            operation = this@Operation
        )
    }

    /**
     * Transitions elements to a new state if they pass a condition based on the element
     */
    fun NavElements<NavTarget, State>.transitionTo(
        newTargetState: State,
        condition: (NavElement<NavTarget, State>) -> Boolean
    ) = map { element ->
        if (condition.invoke(element)) {
            element.transitionTo(
                newTargetState = newTargetState,
                operation = this@Operation
            )
        } else {
            element
        }
    }

    /**
     * Transitions elements to a new state if they pass a condition based on the element and its position
     */
    fun NavElements<NavTarget, State>.transitionToIndexed(
        newTargetState: State,
        condition: (Int, NavElement<NavTarget, State>) -> Boolean
    ) = mapIndexed { index, element ->
        if (condition.invoke(index, element)) {
            element.transitionTo(
                newTargetState = newTargetState,
                operation = this@Operation
            )
        } else {
            element
        }
    }

    // FIXME @Parcelize
    class Noop<NavTarget, State> : Operation<NavTarget, State> {

        override fun isApplicable(elements: NavElements<NavTarget, State>) = false

        override fun invoke(
            elements: NavElements<NavTarget, State>
        ): NavTransition<NavTarget, State> = NavTransition(elements, elements)

        override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}

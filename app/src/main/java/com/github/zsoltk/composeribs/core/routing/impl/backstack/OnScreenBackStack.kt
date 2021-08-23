package com.github.zsoltk.composeribs.core.routing.impl.backstack

class OnScreenBackStack<T>(
    initialElement: T,
) : BackStack<T>(
    initialElement
) {

    /**
     * Never remove anything from screen
     */
    override val offScreen: List<BackStackElement<T>>
        get() = emptyList()

    /**
     * All elements remain on screen
     */
    override val onScreen: List<BackStackElement<T>>
        get() = elements
}

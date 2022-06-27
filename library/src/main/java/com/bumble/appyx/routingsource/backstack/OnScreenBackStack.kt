//package com.bumble.appyx.core.routing.source.backstack
//
//class OnScreenBackStack<T>(
//    initialElement: T,
//) : BackStack<T>(
//    initialElement
//) {
//
//    /**
//     * Never remove anything from screen
//     */
//    override val offScreen: Elements<T>
//        get() = emptyList()
//
//    /**
//     * All elements remain on screen
//     */
//    override val onScreen: Elements<T>
//        get() = elementsObservable
//}

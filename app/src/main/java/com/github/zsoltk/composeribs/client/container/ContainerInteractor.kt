package com.github.zsoltk.composeribs.client.container

import com.github.zsoltk.composeribs.client.container.Container.Routing.*
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack

class ContainerInteractor(
    private val backStack: BackStack<Container.Routing>
) {
    private var localCounter: Int = 1

    fun pushRouting() {
//        val newRouting = when (backStack.currentRouting) {
//            Child1 -> Child2
//            Child2 -> Child1
//        }

//        backStack.push(newRouting)

        backStack.push(Child(i = localCounter++))
    }

    fun popRouting() {
        backStack.pop()
    }
}

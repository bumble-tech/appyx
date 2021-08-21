package com.github.zsoltk.composeribs.client.container

import com.github.zsoltk.composeribs.client.container.Container.Routing.*
import com.github.zsoltk.composeribs.core.BackStack

class ContainerInteractor(
    private val backStack: BackStack<Container.Routing>
) {

    fun pushRouting() {
        val newRouting = when (backStack.currentRouting) {
            Child1 -> Child2
            Child2 -> Child1
        }

        backStack.push(newRouting)
    }

    fun popRouting() {
        backStack.pop()
    }
}

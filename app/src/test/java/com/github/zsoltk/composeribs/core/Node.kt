package com.github.zsoltk.composeribs.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class Node<T>(
    private val view: RibView<T>,
    private val subtreeController: SubtreeController<T>? = null
) {

    /**
     * Notes
     *  - the whole below is to allow off-screen stuff living on
     *  - but as a 1st step maybe it's not needed, and only compose on-screen stuff + bundles
     *  - yes this would kill bg listening and being up-to-date
     *  - but
     *      1. maybe it's a good tradeoff, and re-subscribing to stuff should be sufferable
     *      2. or maybe we can launch from child into parent's scope
     */
    @Composable
    fun Compose(withView: Boolean) {
        subtreeController?.let {
            WithBackStack(withView, it.backStack.elements, it.resolver)
        } ?: run {
            view.Compose(withView, emptyList())
        }
    }

    @Composable
    private fun WithBackStack(withView: Boolean, elements: List<T>, resolver: Resolver<T>) {
        val children = elements.mapIndexed { index, routing ->
            remember(index, routing) { resolver(routing) }
        }

//        view.Compose(withView)
    }

//    private fun SubtreeController<T>.manageRemoved(children: MutableMap<T, ChildEntry>) {
//        children.keys.forEach { routing ->
//            if (!backStack.elements.contains(routing)) {
//                children.remove(routing)
//            }
//        }
//    }

    private fun SubtreeController<T>.manageOffScreen(children: MutableMap<T, ChildEntry>) {
        backStack.offScreen.forEach { routing ->
            children[routing]?.let { entry ->
                // Remove from screen
                if (entry.withView) {
                    children[routing] = entry.copy(withView = false)
                }
            } ?: run {
                // Add as off-screen
                children[routing] = ChildEntry(
                    withView = false,
                    rib = resolver(routing)
                )
            }
        }
    }

    private fun SubtreeController<T>.manageOnScreen(children: MutableMap<T, ChildEntry>) {
        backStack.current.let { routing ->
            children[routing] = ChildEntry(
                withView = true,
                rib = resolver(routing)
            )
        }
    }
}

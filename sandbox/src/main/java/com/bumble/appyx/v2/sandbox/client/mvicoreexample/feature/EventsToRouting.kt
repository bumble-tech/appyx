package com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature

import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.operation.newRoot
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing.Child1
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing.Child2
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreView.Event
import io.reactivex.functions.Consumer

class EventsToRouting(private val backStack: BackStack<Routing>) : Consumer<Event> {

    override fun accept(event: Event) {
        when (event) {
            is Event.SwitchChildClicked -> {
                if (backStack.routings.value == listOf(Child1)) {
                    backStack.newRoot(Child2)
                } else {
                    backStack.newRoot(Child1)
                }
            }
            else -> Unit
        }
    }
}

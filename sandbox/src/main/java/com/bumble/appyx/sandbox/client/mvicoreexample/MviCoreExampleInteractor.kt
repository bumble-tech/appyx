package com.bumble.appyx.sandbox.client.mvicoreexample

import androidx.lifecycle.Lifecycle
import com.badoo.binder.using
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.mvicore.feature.Feature
import com.bumble.appyx.core.children.whenChildAttached
import com.bumble.appyx.core.clienthelper.interactor.Interactor
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.activeRouting
import com.bumble.appyx.routingsource.backstack.operation.newRoot
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing.Child1
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing.Child2
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.EventsToWish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.News
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.OutputChild1ToWish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.OutputChild2ToWish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.StateToViewModel
import io.reactivex.functions.Consumer

class MviCoreExampleInteractor(
    private val view: MviCoreExampleView,
    private val feature: Feature<Wish, State, News>,
    private val backStack: BackStack<Routing>,
) : Interactor<MviCoreExampleNode>() {

    private val backStackUpdater = Consumer<Event> { event ->
        when (event) {
            is Event.SwitchChildClicked -> {
                if (backStack.activeRouting == Child1) {
                    backStack.newRoot(Child2)
                } else {
                    backStack.newRoot(Child1)
                }
            }
            else -> Unit
        }
    }

    override fun onCreate(lifecycle: Lifecycle) {
        lifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using EventsToWish)
            bind(view to backStackUpdater)
        }
        whenChildAttached { _: Lifecycle, child: Node ->
            child.lifecycle.createDestroy {
                when (child) {
                    is MviCoreChildNode1 -> bind(child.output to feature using OutputChild1ToWish)
                    is MviCoreChildNode2 -> bind(child.output to feature using OutputChild2ToWish)
                }
            }
        }
    }
}

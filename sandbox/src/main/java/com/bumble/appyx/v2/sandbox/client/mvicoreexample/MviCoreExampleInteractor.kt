package com.bumble.appyx.v2.sandbox.client.mvicoreexample

import androidx.lifecycle.Lifecycle
import com.badoo.binder.using
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.android.lifecycle.startStop
import com.bumble.appyx.v2.core.children.whenChildAttached
import com.bumble.appyx.v2.core.clienthelper.interactor.Interactor
import com.bumble.appyx.v2.core.node.Node
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.EventsToRouting
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.EventsToWish
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.OutputChild1ToWish
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.OutputChild2ToWish
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.StateToViewModel

class MviCoreExampleInteractor(
    private val view: MviCoreView,
    private val feature: MviCoreExampleFeature,
    private val backStack: BackStack<Routing>,
) : Interactor<MviCoreExampleNode>() {

    override fun onCreate(lifecycle: Lifecycle) {
        lifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using EventsToWish)
            bind(view to EventsToRouting(backStack))
        }
        whenChildAttached { _: Lifecycle, child: Node ->
            when (child) {
                is MviCoreChildNode1 -> {
                    child.lifecycle.createDestroy {
                        bind(child.output to feature using OutputChild1ToWish)
                    }
                }
                is MviCoreChildNode2 -> {
                    child.lifecycle.createDestroy {
                        bind(child.output to feature using OutputChild2ToWish)
                    }
                }
            }
        }
    }
}

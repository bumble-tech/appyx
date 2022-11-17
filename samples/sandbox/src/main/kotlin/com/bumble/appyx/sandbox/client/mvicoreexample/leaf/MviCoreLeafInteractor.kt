package com.bumble.appyx.sandbox.client.mvicoreexample.leaf

import androidx.lifecycle.Lifecycle
import com.badoo.binder.using
import com.badoo.mvicore.android.lifecycle.startStop
import com.badoo.mvicore.feature.Feature
import com.bumble.appyx.core.clienthelper.interactor.Interactor
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.EventsToWish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.News
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.Wish
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.StateToViewModel

class MviCoreLeafInteractor(
    private val view: MviCoreLeafView,
    private val feature: Feature<Wish, State, News>
) : Interactor<MviCoreLeafNode>() {

    override fun onCreate(lifecycle: Lifecycle) {
        lifecycle.startStop {
            bind(feature to view using StateToViewModel)
            bind(view to feature using EventsToWish)
        }
    }
}

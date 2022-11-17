package com.bumble.appyx.sandbox.client.mvicoreexample.feature

import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State.Finished
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State.InitialState
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State.Loaded
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.MviCoreExampleFeature.State.Loading

internal object StateToViewModel : (State) -> ViewModel? {

    override fun invoke(state: State): ViewModel? =
        when (state) {
            is Loading -> ViewModel.Loading
            is InitialState -> ViewModel.InitialState(state.stateName)
            is Loaded -> ViewModel.Loaded(state.stateName)
            is Finished -> null
        }
}

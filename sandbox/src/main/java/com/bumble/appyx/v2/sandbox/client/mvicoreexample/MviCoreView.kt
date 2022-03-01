package com.bumble.appyx.v2.sandbox.client.mvicoreexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.v2.core.composable.Children
import com.bumble.appyx.v2.core.node.AbstractNodeVew
import com.bumble.appyx.v2.core.routing.source.backstack.BackStack
import com.bumble.appyx.v2.core.routing.source.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreView.Event
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreView.Event.LoadDataClicked
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreView.Event.SwitchChildClicked
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.ViewModel
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.ViewModel.InitialState
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.ViewModel.Loaded
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.ViewModel.Loading
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

class MviCoreView(
    private val backStack: BackStack<Routing>,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : AbstractNodeVew<MviCoreExampleNode>(), ObservableSource<Event> by events, Consumer<ViewModel> {

    sealed class Event {
        object LoadDataClicked : Event()
        object SwitchChildClicked : Event()
    }

    private var vm by mutableStateOf<ViewModel?>(null)

    override fun accept(vm: ViewModel) {
        this.vm = vm
    }

    @Composable
    override fun MviCoreExampleNode.NodeView(modifier: Modifier) {
        val viewModel = vm ?: return
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Children(
                transitionHandler = rememberBackstackSlider(),
                modifier = modifier
                    .fillMaxWidth()
                    .requiredHeight(200.dp),
                routingSource = backStack
            )
            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                onClick = { events.accept(SwitchChildClicked) }
            ) {
                Text(text = "Switch between children")
            }
            when (viewModel) {
                is Loading -> Box(modifier = modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is InitialState ->
                    Box(modifier = modifier.fillMaxSize()) {
                        Column(modifier = modifier.align(Alignment.Center)) {
                            Text(text = viewModel.stateName)
                            Spacer(modifier = Modifier.requiredHeight(8.dp))
                            Button(onClick = { events.accept(LoadDataClicked) }) {
                                Text(text = "Load data")
                            }
                        }
                    }
                is Loaded ->
                    Box(modifier = modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = viewModel.stateName
                        )
                    }
            }
        }
    }
}

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
import com.bumble.appyx.v2.core.routing.source.backstack.operation.newRoot
import com.bumble.appyx.v2.core.routing.source.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing.Child1
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing.Child2
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreView.Event
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.MviCoreView.Event.LoadDataClicked
import com.bumble.appyx.v2.sandbox.client.mvicoreexample.feature.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

class MviCoreView(
    private val events: PublishRelay<Event> = PublishRelay.create()
) : ObservableSource<Event> by events, Consumer<ViewModel> {

    sealed class Event {
        object LoadDataClicked : Event()
    }

    private var vm by mutableStateOf<ViewModel?>(null)

    private var index: Int = 0

    @Composable
    fun MviCoreView(modifier: Modifier, node: MviCoreExampleNode) {
        node.MviCoreView(modifier = modifier, viewModel = vm)
    }

    @Composable
    private fun MviCoreExampleNode.MviCoreView(modifier: Modifier, viewModel: ViewModel?) {
        viewModel ?: return
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
                onClick = {
                    if (backStack.routings.value == listOf(Child2)) {
                        backStack.newRoot(Child1)
                    } else {
                        backStack.newRoot(Child2)
                    }
                }
            ) {
                Text(text = "Switch between children")
            }
            when (viewModel) {
                is ViewModel.Loading -> Box(modifier = modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ViewModel.InitialState ->
                    Box(modifier = modifier.fillMaxSize()) {
                        Column(modifier = modifier.align(Alignment.Center)) {
                            Text(text = viewModel.stateName)
                            Spacer(modifier = Modifier.requiredHeight(8.dp))
                            Button(onClick = { events.accept(LoadDataClicked) }) {
                                Text(text = "Load data")
                            }
                        }
                    }
                is ViewModel.Loaded ->
                    Box(modifier = modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = viewModel.stateName
                        )
                    }
            }
        }
    }

    override fun accept(vm: ViewModel) {
        this.vm = vm
    }
}

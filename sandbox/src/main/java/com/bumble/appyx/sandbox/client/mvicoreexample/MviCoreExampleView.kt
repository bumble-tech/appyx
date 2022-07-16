package com.bumble.appyx.sandbox.client.mvicoreexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.ParentNodeView
import com.bumble.appyx.core.routing.RoutingSource
import com.bumble.appyx.routingsource.backstack.BackStack
import com.bumble.appyx.routingsource.backstack.transitionhandler.rememberBackstackSlider
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleNode.Routing
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event.LoadDataClicked
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl.Event.SwitchChildClicked
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel.InitialState
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel.Loaded
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel.Loading
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface MviCoreExampleView : Consumer<ViewModel>, ObservableSource<Event>

class MviCoreExampleViewImpl(
    private val title: String = "Title",
    private val backStack: RoutingSource<Routing, BackStack.TransitionState>,
    private val events: PublishRelay<Event> = PublishRelay.create()
) : ParentNodeView<Routing>(),
    MviCoreExampleView,
    ObservableSource<Event> by events {

    sealed class Event {
        object LoadDataClicked : Event()
        object SwitchChildClicked : Event()
    }

    private var vm by mutableStateOf<ViewModel?>(null)

    override fun accept(vm: ViewModel) {
        this.vm = vm
    }

    @Composable
    override fun ParentNode<Routing>.NodeView(modifier: Modifier) {
        val viewModel = vm ?: return
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier
                    .testTag(TitleTag)
            )
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
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag(LoadingTestTag)
                    )
                }
                is InitialState ->
                    Box(modifier = modifier.fillMaxSize()) {
                        Column(modifier = modifier.align(Alignment.Center)) {
                            Text(
                                modifier = Modifier.testTag(InitialStateTextTag),
                                color = Color.Black, text = viewModel.stateName
                            )
                            Spacer(modifier = Modifier.requiredHeight(8.dp))
                            Button(
                                modifier = Modifier.testTag(InitialStateButtonTag),
                                onClick = { events.accept(LoadDataClicked) }
                            ) {
                                Text(
                                    modifier = Modifier.testTag(InitialStateButtonTextTag),
                                    text = "Load data"
                                )
                            }
                        }
                    }
                is Loaded ->
                    Box(modifier = modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Black,
                            text = viewModel.stateName
                        )
                    }
            }
        }
    }

    companion object {
        const val TitleTag = "Title"
        const val LoadingTestTag = "Loading"
        const val InitialStateTextTag = "InitialStateText"
        const val InitialStateButtonTag = "InitialStateButton"
        const val InitialStateButtonTextTag = "InitialStateButton"
    }
}

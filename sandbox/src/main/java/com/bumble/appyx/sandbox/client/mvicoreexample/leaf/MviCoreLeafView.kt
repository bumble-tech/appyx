package com.bumble.appyx.sandbox.client.mvicoreexample.leaf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.core.node.NodeView
import com.bumble.appyx.sandbox.client.mvicoreexample.MviCoreExampleViewImpl
import com.bumble.appyx.sandbox.client.mvicoreexample.feature.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface MviCoreLeafView : Consumer<ViewModel>, ObservableSource<MviCoreExampleViewImpl.Event>

class MviCoreLeafViewImpl(
    private val title: String = "Leaf Node",
    private val events: PublishRelay<MviCoreExampleViewImpl.Event> = PublishRelay.create()
) : NodeView,
    MviCoreLeafView,
    ObservableSource<MviCoreExampleViewImpl.Event> by events {

    private var vm by mutableStateOf<ViewModel?>(null)

    override fun accept(vm: ViewModel) {
        this.vm = vm
    }

    @Composable
    override fun View(modifier: Modifier) {
        val viewModel = vm ?: return

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier
                    .testTag(MviCoreExampleViewImpl.TitleTag)
            )

            when (viewModel) {
                is ViewModel.Loading -> Box(modifier = modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag(MviCoreExampleViewImpl.LoadingTestTag)
                    )
                }
                is ViewModel.InitialState ->
                    Box(modifier = modifier.fillMaxSize()) {
                        Column(modifier = modifier.align(Alignment.Center)) {
                            Text(
                                modifier = Modifier.testTag(MviCoreExampleViewImpl.InitialStateTextTag),
                                color = Color.Black, text = viewModel.stateName
                            )
                            Spacer(modifier = Modifier.requiredHeight(8.dp))
                            Button(
                                modifier = Modifier.testTag(MviCoreExampleViewImpl.InitialStateButtonTag),
                                onClick = { events.accept(MviCoreExampleViewImpl.Event.LoadDataClicked) }
                            ) {
                                Text(
                                    modifier = Modifier.testTag(MviCoreExampleViewImpl.InitialStateButtonTextTag),
                                    text = "Load data"
                                )
                            }
                        }
                    }
                is ViewModel.Loaded ->
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
}

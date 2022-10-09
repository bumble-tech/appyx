package com.bumble.appyx.sandbox2.navmodel2

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.navigation2.inputsource.AnimatedInputSource
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.*
import com.bumble.appyx.core.navigation2.inputsource.ManualProgressInputSource
import com.bumble.appyx.navmodel2.spotlight.Spotlight
import com.bumble.appyx.navmodel2.spotlight.operation.First
import com.bumble.appyx.navmodel2.spotlight.operation.Last
import com.bumble.appyx.navmodel2.spotlight.operation.Next
import com.bumble.appyx.navmodel2.spotlight.operation.Previous
import com.bumble.appyx.navmodel2.spotlight.operation.next
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import com.bumble.appyx.navmodel2.spotlight.transitionhandler.SpotlightSlider
import com.bumble.appyx.sandbox2.ui.theme.appyx_dark


private val spotlight = Spotlight(
    items = listOf(Child1, Child2, Child3, Child4, Child5, Child6, Child7),
)


@ExperimentalMaterialApi
@Composable
fun SpotlightExperiment() {
    val coroutineScope = rememberCoroutineScope()
    val inputSource = remember { AnimatedInputSource(spotlight, coroutineScope) }

    var elementSize by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by createTransitionParams(elementSize)
    val uiProps = remember(transitionParams) { SpotlightSlider<NavTarget>(transitionParams) }
    val render = remember(uiProps) { spotlight.elements.map { uiProps.map(it) } }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Children(
            render = render.collectAsState(listOf()),
            modifier = Modifier
                .weight(0.9f)
                .padding(64.dp)
            ,
            onElementSizeChanged = { elementSize = it }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(4.dp)
            ,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { inputSource.operation(First()) }) {
                Text("First")
            }
            Button(onClick = { inputSource.operation(Previous()) }) {
                Text("Prev")
            }
            Button(onClick = { inputSource.operation(Next()) }) {
                Text("Next")
            }
            Button(onClick = { inputSource.operation(Last()) }) {
                Text("Last")
            }
        }
    }
}

package com.bumble.appyx.sandbox2.navmodel2

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
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
import com.bumble.appyx.navmodel2.spotlight.Spotlight
import com.bumble.appyx.navmodel2.spotlight.operation.first
import com.bumble.appyx.navmodel2.spotlight.operation.last
import com.bumble.appyx.navmodel2.spotlight.operation.next
import com.bumble.appyx.navmodel2.spotlight.operation.previous
import com.bumble.appyx.navmodel2.spotlight.transitionhandler.SpotlightSlider
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child1
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child2
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child3
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child4
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child5
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child6
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child7
import kotlinx.coroutines.flow.map


private val spotlight = Spotlight(
    items = listOf(Child1, Child2, Child3, Child4, Child5, Child6, Child7),
)


@ExperimentalMaterialApi
@Composable
fun SpotlightExperiment() {
    val coroutineScope = rememberCoroutineScope()
    val inputSource = remember {
        AnimatedInputSource(
            navModel = spotlight,
            coroutineScope = coroutineScope,
            defaultAnimationSpec = spring(
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    var elementSize by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by createTransitionParams(elementSize)
    val uiProps = remember(transitionParams) { SpotlightSlider<NavTarget>(
        transitionParams = transitionParams,
        orientation = Orientation.Horizontal
    ) }
    val render = remember(uiProps) { spotlight.segments.map { uiProps.map(it) } }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Children(
            render = render.collectAsState(listOf()),
            modifier = Modifier.weight(0.9f),
            onElementSizeChanged = { elementSize = it },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { inputSource.first() }) {
                Text("First")
            }
            Button(onClick = { inputSource.previous(spring(stiffness = Spring.StiffnessLow)) }) {
                Text("Prev")
            }
            Button(onClick = { inputSource.next(spring(stiffness = Spring.StiffnessMedium)) }) {
                Text("Next")
            }
            Button(onClick = { inputSource.last() }) {
                Text("Last")
            }
        }
    }
}

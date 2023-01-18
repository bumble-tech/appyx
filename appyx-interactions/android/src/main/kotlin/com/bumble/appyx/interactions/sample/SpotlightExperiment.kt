package com.bumble.appyx.interactions.sample

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.Logger
import com.bumble.appyx.interactions.sample.NavTarget.Child1
import com.bumble.appyx.interactions.sample.NavTarget.Child2
import com.bumble.appyx.interactions.sample.NavTarget.Child3
import com.bumble.appyx.interactions.sample.NavTarget.Child4
import com.bumble.appyx.interactions.sample.NavTarget.Child5
import com.bumble.appyx.interactions.sample.NavTarget.Child6
import com.bumble.appyx.interactions.sample.NavTarget.Child7
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.interpolator.SpotlightSlider
import com.bumble.appyx.transitionmodel.spotlight.operation.first
import com.bumble.appyx.transitionmodel.spotlight.operation.last
import com.bumble.appyx.transitionmodel.spotlight.operation.next
import com.bumble.appyx.transitionmodel.spotlight.operation.previous


@ExperimentalMaterialApi
@Composable
fun SpotlightExperiment() {
    val density = LocalDensity.current
    var elementSize by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by createTransitionParams(elementSize)
    val coroutineScope = rememberCoroutineScope()

    val spotlight = remember {
        Spotlight(
            scope = coroutineScope,
            model = SpotlightModel(items = listOf(Child1, Child2, Child3, Child4, Child5, Child6, Child7)),
            propsMapper = SpotlightSlider(transitionParams),
            gestureFactory = SpotlightSlider.Gestures(transitionParams),
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
    }

    Column(
        Modifier
            .fillMaxWidth()
            .background(appyx_dark)
    ) {
        Children(
            frameModel = spotlight.frames.collectAsState(listOf()),
            modifier = Modifier
                .weight(0.9f)
                .padding(
                    horizontal = 64.dp,
                    vertical = 12.dp
                ),
            onElementSizeChanged = { elementSize = it },
            element = {
                Element(
                    frameModel = it,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(it.navElement.key) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    spotlight.onDrag(dragAmount, density)
                                },
                                onDragEnd = {
                                    Logger.log("drag", "end")
                                    spotlight.onDragEnd()
                                }
                            )
                        }
                )
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { spotlight.first() }) {
                Text("First")
            }
            Button(onClick = { spotlight.previous(spring(stiffness = Spring.StiffnessLow)) }) {
                Text("Prev")
            }
            Button(onClick = { spotlight.next(spring(stiffness = Spring.StiffnessMedium)) }) {
                Text("Next")
            }
            Button(onClick = { spotlight.last() }) {
                Text("Last")
            }
        }
    }
}


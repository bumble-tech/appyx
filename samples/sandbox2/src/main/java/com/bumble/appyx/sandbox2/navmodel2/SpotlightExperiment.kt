package com.bumble.appyx.sandbox2.navmodel2

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
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
import com.bumble.appyx.core.navigation2.inputsource.AnimatedInputSource
import com.bumble.appyx.core.navigation2.inputsource.DragProgressInputSource
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
import com.bumble.appyx.sandbox2.ui.theme.appyx_dark
import kotlinx.coroutines.flow.map


@ExperimentalMaterialApi
@Composable
fun SpotlightExperiment() {
    val spotlight = remember {
        Spotlight(
            items = listOf(Child1, Child2, Child3, Child4, Child5, Child6, Child7),
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val drag = remember { DragProgressInputSource(spotlight, coroutineScope) }
    val defaultAnimationSpec = spring<Float>(stiffness = Spring.StiffnessMediumLow)
    val animated = remember {
        AnimatedInputSource(
            spotlight, coroutineScope, defaultAnimationSpec
        )
    }

    val density = LocalDensity.current
    var elementSize by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by createTransitionParams(elementSize)
    val uiProps = remember(transitionParams) {
        SpotlightSlider<NavTarget>(
            transitionParams = transitionParams,
            orientation = Orientation.Horizontal
        )
    }
    val render = remember(uiProps) { spotlight.segments.map { uiProps.map(it) } }

    Column(
        Modifier
            .fillMaxWidth()
            .background(appyx_dark)
    ) {
        Children(
            renderParams = render.collectAsState(listOf()),
            modifier = Modifier
                .weight(0.9f)
                .padding(
                    horizontal = 64.dp,
                    vertical = 12.dp
                ),
            onElementSizeChanged = { elementSize = it },
            element = {
                Element(
                    renderParams = it,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(it.navElement.key) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    if (drag.gestureFactory == null) {
                                        drag.gestureFactory = { dragAmount ->
                                            uiProps.createGesture(dragAmount, density)
                                        }
                                    }
                                    drag.addDeltaProgress(dragAmount)
                                },
                                onDragEnd = {
                                    Log.d("drag", "end")
                                    drag.gestureFactory = null
                                    drag.settle(
                                        roundUpAnimationSpec = defaultAnimationSpec,
                                        roundDownAnimationSpec = defaultAnimationSpec
                                    )
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
            Button(onClick = { animated.first() }) {
                Text("First")
            }
            Button(onClick = { animated.previous(spring(stiffness = Spring.StiffnessLow)) }) {
                Text("Prev")
            }
            Button(onClick = { animated.next(spring(stiffness = Spring.StiffnessMedium)) }) {
                Text("Next")
            }
            Button(onClick = { animated.last() }) {
                Text("Last")
            }
        }
    }
}

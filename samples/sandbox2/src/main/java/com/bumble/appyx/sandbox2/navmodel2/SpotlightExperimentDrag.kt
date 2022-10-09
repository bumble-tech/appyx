package com.bumble.appyx.sandbox2.navmodel2

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.bumble.appyx.core.navigation2.inputsource.ManualProgressInputSource
import com.bumble.appyx.navmodel2.spotlight.Spotlight
import com.bumble.appyx.navmodel2.spotlight.operation.next
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


private val spotlight = Spotlight(
    items = listOf(Child1, Child2, Child3, Child4, Child5, Child6, Child7),
)


@ExperimentalMaterialApi
@Composable
fun SpotlightExperimentDrag() {
    val coroutineScope = rememberCoroutineScope()
    val inputSource = remember { ManualProgressInputSource(spotlight, coroutineScope) }

    LaunchedEffect(Unit) {
        // TODO auto-load the next operation based on gesture
        inputSource.next()
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
    val render = remember(uiProps) { spotlight.elements.map { uiProps.map(it) } }

    Column(
        Modifier
            .fillMaxWidth()
            .background(appyx_dark)
    ) {
        KnobControl(onValueChange = {
            inputSource.setNormalisedProgress(it)
        })

        Children(
            render = render.collectAsState(listOf()),
            onElementSizeChanged = { elementSize = it },
            element = {
                Element(
                    render = it,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            // TODO might be better with swipeable for its anchors,
                            //  then .settle wouldn't be needed. However, settle might be needed
                            //  anyway in other scenarios (a touch to stop a fling, followed by a
                            //  release)
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val delta = uiProps.calculateProgress(dragAmount, density)
                                    // FIXME this moves normalised progress towards maxProgress
                                    //  under the hood rather than to next segment!
                                    inputSource.addDeltaProgress(delta)
                                },
                                onDragEnd = {
                                    Log.d("drag", "end")
                                    inputSource.settle()
                                }
                            )
                        }
                )
            }
        )
    }
}

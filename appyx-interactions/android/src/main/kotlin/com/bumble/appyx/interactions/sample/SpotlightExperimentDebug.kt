package com.bumble.appyx.interactions.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.inputsource.DebuglProgressInputSource
import com.bumble.appyx.interactions.sample.NavTarget.Child1
import com.bumble.appyx.interactions.sample.NavTarget.Child2
import com.bumble.appyx.interactions.sample.NavTarget.Child3
import com.bumble.appyx.interactions.sample.NavTarget.Child4
import com.bumble.appyx.interactions.sample.NavTarget.Child5
import com.bumble.appyx.interactions.sample.NavTarget.Child6
import com.bumble.appyx.interactions.sample.NavTarget.Child7
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.interpolator.SpotlightSlider
import com.bumble.appyx.transitionmodel.spotlight.operation.first
import com.bumble.appyx.transitionmodel.spotlight.operation.last
import com.bumble.appyx.transitionmodel.spotlight.operation.next
import com.bumble.appyx.transitionmodel.spotlight.operation.previous
import kotlinx.coroutines.flow.map


@ExperimentalMaterialApi
@Composable
fun SpotlightExperimentDebug() {
    val spotlight = remember {
        Spotlight(
            items = listOf(Child1, Child2, Child3, Child4, Child5, Child6, Child7),
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val inputSource = remember { DebuglProgressInputSource(spotlight, coroutineScope) }

    LaunchedEffect(Unit) {
        inputSource.next()
        inputSource.next()
        inputSource.next()
        inputSource.previous()
        inputSource.last()
        inputSource.first()
    }

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
        KnobControl(onValueChange = {
            inputSource.setNormalisedProgress(it)
        })

        Children(
            modifier = Modifier.padding(
                horizontal = 64.dp,
                vertical = 12.dp
            ),
            frameModel = render.collectAsState(listOf()),
            onElementSizeChanged = { elementSize = it },
            element = {
                Element(
                    frameModel = it,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        )
    }
}

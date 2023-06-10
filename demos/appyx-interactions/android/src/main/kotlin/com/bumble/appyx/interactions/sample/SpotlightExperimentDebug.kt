package com.bumble.appyx.interactions.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.gestures.Gestures
import com.bumble.appyx.components.spotlight.operation.first
import com.bumble.appyx.components.spotlight.operation.last
import com.bumble.appyx.components.spotlight.operation.next
import com.bumble.appyx.components.spotlight.operation.previous
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.sample.android.Children
import com.bumble.appyx.interactions.sample.android.Element
import com.bumble.appyx.interactions.theme.appyx_dark

@ExperimentalMaterialApi
@Composable
fun SpotlightExperimentDebug(modifier: Modifier = Modifier) {
    val spotlight = remember {
        Spotlight(
            model = SpotlightModel(
                items = listOf(
                    InteractionTarget.Child1,
                    InteractionTarget.Child2,
                    InteractionTarget.Child3,
                    InteractionTarget.Child4,
                    InteractionTarget.Child5,
                    InteractionTarget.Child6,
                    InteractionTarget.Child7
                ),
                savedStateMap = null
            ),
            motionController = { SpotlightSlider(it) },
            gestureFactory = { Gestures(it) },
            isDebug = true
        )
    }

    InteractionModelSetup(spotlight)


    LaunchedEffect(Unit) {
        spotlight.next()
        spotlight.next()
        spotlight.next()
        spotlight.previous()
        spotlight.last()
        spotlight.first()
    }

    Column(
        Modifier
            .fillMaxWidth()
            .background(appyx_dark)
    ) {
        KnobControl(onValueChange = {
            spotlight.setNormalisedProgress(it)
        })

        Children(
            modifier = Modifier.padding(
                horizontal = 64.dp,
                vertical = 12.dp
            ),
            interactionModel = spotlight,
            element = {
                Element(
                    elementUiModel = it,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        )
    }
}

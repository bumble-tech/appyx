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
import com.bumble.appyx.interactions.core.ui.InteractionModelSetup
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
fun SpotlightExperimentDebug() {
    val spotlight = remember {
        Spotlight(
            model = SpotlightModel(
                items = listOf(Child1, Child2, Child3, Child4, Child5, Child6, Child7)
            ),
            motionController = { SpotlightSlider(it) },
            gestureFactory = { SpotlightSlider.Gestures(it) },
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
                    frameModel = it,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        )
    }
}

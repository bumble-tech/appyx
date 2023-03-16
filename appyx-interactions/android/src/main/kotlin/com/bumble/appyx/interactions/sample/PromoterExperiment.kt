package com.bumble.appyx.interactions.sample

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.sample.NavTarget.Child1
import com.bumble.appyx.interactions.sample.NavTarget.Child2
import com.bumble.appyx.interactions.sample.NavTarget.Child3
import com.bumble.appyx.interactions.sample.NavTarget.Child4
import com.bumble.appyx.transitionmodel.promoter.Promoter
import com.bumble.appyx.transitionmodel.promoter.PromoterModel
import com.bumble.appyx.transitionmodel.promoter.interpolator.PromoterMotionController
import com.bumble.appyx.transitionmodel.promoter.operation.addFirst


@ExperimentalMaterialApi
@Composable
fun PromoterExperiment() {
    val coroutineScope = rememberCoroutineScope()

    val promoter = remember {
        Promoter(
            scope = coroutineScope,
            model = PromoterModel<NavTarget>(),
            motionController = {
                PromoterMotionController(
                    uiContext = it,
                    childSize = 100.dp,
                )
            },
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 20)
        )
    }

    LaunchedEffect(Unit) {
        promoter.addFirst(Child1)
        promoter.addFirst(Child2)
        promoter.addFirst(Child3)
        promoter.addFirst(Child4)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Children(
            interactionModel = promoter,
            modifier = Modifier
                .weight(0.9f)
                .padding(
                    horizontal = 64.dp,
                    vertical = 12.dp
                ),
            element = {
                Element(
                    elementUiModel = it,
                    modifier = Modifier.size(100.dp)
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
            Button(
                onClick = { promoter.addFirst(NavTarget.values().random()) }
            ) {
                Text("Add")
            }
        }
    }
}

package com.bumble.appyx.interactions.sample

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.sample.NavTarget.*
import com.bumble.appyx.transitionmodel.promoter.Promoter
import com.bumble.appyx.transitionmodel.promoter.PromoterModel
import com.bumble.appyx.transitionmodel.promoter.interpolator.PromoterProps
import com.bumble.appyx.transitionmodel.promoter.operation.addFirst


@ExperimentalMaterialApi
@Composable
fun PromoterExperiment() {
    var elementSize by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by createTransitionParams(elementSize)
    val coroutineScope = rememberCoroutineScope()

    val promoter = remember {
        Promoter(
            scope = coroutineScope,
            model = PromoterModel<NavTarget>(),
            propsMapper = PromoterProps(
                childSize = 100.dp,
                transitionParams = transitionParams
            ),
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
            frameModel = promoter.frames.collectAsState(listOf()),
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

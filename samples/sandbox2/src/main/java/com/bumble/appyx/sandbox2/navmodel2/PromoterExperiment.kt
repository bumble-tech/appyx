package com.bumble.appyx.sandbox2.navmodel2

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
import com.bumble.appyx.navmodel.promoter.navmodel2.operation.addFirst
import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter
import com.bumble.appyx.navmodel.promoter.navmodel2.transitionhandler.PromoterProps
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child1
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child2
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child3
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child4
import kotlinx.coroutines.flow.map


@ExperimentalMaterialApi
@Composable
fun PromoterExperiment() {
    val promoter = remember { Promoter<NavTarget>() }
    val coroutineScope = rememberCoroutineScope()
    val inputSource: AnimatedInputSource<NavTarget, Promoter.State> = remember {
        AnimatedInputSource(
            navModel = promoter,
            coroutineScope = coroutineScope,
            defaultAnimationSpec = spring(
                stiffness = Spring.StiffnessVeryLow / 20
            )
        )
    }

    LaunchedEffect(Unit) {
        inputSource.addFirst(Child1)
        inputSource.addFirst(Child2)
        inputSource.addFirst(Child3)
        inputSource.addFirst(Child4)
    }

    var elementSize by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by createTransitionParams(elementSize)
    val uiProps = remember(transitionParams) { PromoterProps<NavTarget>(
        childSize = 100.dp,
        transitionParams = transitionParams
    ) }
    val render = remember(uiProps) { promoter.segments.map { uiProps.map(it) } }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Children(
            renderParams = render.collectAsState(listOf()),
            modifier = Modifier.weight(0.9f),
            onElementSizeChanged = { elementSize = it },
            element = {
                Element(
                    renderParams = it,
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
                onClick = { inputSource.addFirst(NavTarget.values().random()) }
            ) {
                Text("Add")
            }
        }
    }
}

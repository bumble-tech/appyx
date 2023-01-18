package com.bumble.appyx.sandbox2.navmodel2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import com.bumble.appyx.interactions.core.inputsource.DebugProgressInputSource
import com.bumble.appyx.interactions.sample.Children
import com.bumble.appyx.interactions.sample.KnobControl
import com.bumble.appyx.interactions.sample.NavTarget
import com.bumble.appyx.interactions.sample.NavTarget.Child1
import com.bumble.appyx.interactions.sample.NavTarget.Child2
import com.bumble.appyx.interactions.sample.NavTarget.Child3
import com.bumble.appyx.interactions.sample.NavTarget.Child4
import com.bumble.appyx.interactions.sample.NavTarget.Child5
import com.bumble.appyx.interactions.sample.NavTarget.Child6
import com.bumble.appyx.interactions.sample.createTransitionParams
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.interpolator.BackStackSlider
import com.bumble.appyx.transitionmodel.backstack.operation.NewRoot
import com.bumble.appyx.transitionmodel.backstack.operation.Pop
import com.bumble.appyx.transitionmodel.backstack.operation.Push
import com.bumble.appyx.transitionmodel.backstack.operation.Replace
import kotlinx.coroutines.flow.map


@ExperimentalMaterialApi
@Composable
fun BackStackExperimentDebug() {
    val backStack = remember {
        BackStack(
            initialElement = Child1,
            savedStateMap = null
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val inputSource = remember { DebugProgressInputSource(backStack, coroutineScope) }

    LaunchedEffect(Unit) {
        inputSource.operation(Push(Child2))
        inputSource.operation(Push(Child3))
        inputSource.operation(Push(Child4))
        inputSource.operation(Push(Child5))
        inputSource.operation(Replace(Child6))
        inputSource.operation(Pop())
        inputSource.operation(NewRoot(Child1))
    }

    val uiProps = remember { BackStackSlider<NavTarget>() }
    val render = remember(uiProps) { backStack.segments.map { uiProps.map(it) } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(appyx_dark)
    ) {
        KnobControl(onValueChange = {
            inputSource.setNormalisedProgress(it)
        })

        // FIXME
//        Children(
//            modifier = Modifier.padding(
//                horizontal = 64.dp,
//                vertical = 12.dp
//            ),
//            frameModel = render.collectAsState(listOf()),
//            onElementSizeChanged = { elementSize = it }
//        )
    }
}

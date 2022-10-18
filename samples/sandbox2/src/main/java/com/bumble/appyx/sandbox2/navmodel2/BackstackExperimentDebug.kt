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
import com.bumble.appyx.core.navigation2.inputsource.DebuglProgressInputSource
import com.bumble.appyx.navmodel2.backstack.BackStack
import com.bumble.appyx.navmodel2.backstack.operation.NewRoot
import com.bumble.appyx.navmodel2.backstack.operation.Pop
import com.bumble.appyx.navmodel2.backstack.operation.Push
import com.bumble.appyx.navmodel2.backstack.operation.Replace
import com.bumble.appyx.navmodel2.backstack.transitionhandler.BackStackSlider
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child1
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child2
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child3
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child4
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child5
import com.bumble.appyx.sandbox2.navmodel2.NavTarget.Child6
import com.bumble.appyx.sandbox2.ui.theme.appyx_dark
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
    val inputSource = remember { DebuglProgressInputSource(backStack, coroutineScope) }

    LaunchedEffect(Unit) {
        inputSource.operation(Push(Child2))
        inputSource.operation(Push(Child3))
        inputSource.operation(Push(Child4))
        inputSource.operation(Push(Child5))
        inputSource.operation(Replace(Child6))
        inputSource.operation(Pop())
        inputSource.operation(NewRoot(Child1))
    }

    var elementSize by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by createTransitionParams(elementSize)
    val uiProps = remember(transitionParams) { BackStackSlider<NavTarget>(transitionParams) }
    val render = remember(uiProps) { backStack.segments.map { uiProps.map(it) } }

    Column(
        modifier = Modifier
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
            renderParams = render.collectAsState(listOf()),
            onElementSizeChanged = { elementSize = it }
        )
    }
}

package com.bumble.appyx.interactions.sample

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
import com.bumble.appyx.interactions.sample.NavTarget.Child1
import com.bumble.appyx.interactions.sample.NavTarget.Child2
import com.bumble.appyx.interactions.sample.NavTarget.Child3
import com.bumble.appyx.interactions.sample.NavTarget.Child4
import com.bumble.appyx.interactions.sample.NavTarget.Child5
import com.bumble.appyx.interactions.sample.NavTarget.Child6
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.interpolator.BackStackSlider
import com.bumble.appyx.transitionmodel.backstack.operation.newRoot
import com.bumble.appyx.transitionmodel.backstack.operation.pop
import com.bumble.appyx.transitionmodel.backstack.operation.push
import com.bumble.appyx.transitionmodel.backstack.operation.replace


@ExperimentalMaterialApi
@Composable
fun BackStackExperimentDebug() {
    var elementSize by remember { mutableStateOf(IntSize(0, 0)) }
    val transitionParams by createTransitionParams(elementSize)
    val coroutineScope = rememberCoroutineScope()

    val backStack = remember {
        BackStack(
            scope = coroutineScope,
            model = BackStackModel(
                initialElement = Child1,
                savedStateMap = null
            ),
            propsMapper = BackStackSlider(transitionParams),
            isDebug = true
        )
    }

    LaunchedEffect(Unit) {
        backStack.push(Child2)
        backStack.push(Child3)
        backStack.push(Child4)
        backStack.push(Child5)
        backStack.replace(Child6)
        backStack.pop()
        backStack.newRoot(Child1)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(appyx_dark)
    ) {
        KnobControl(onValueChange = {
            backStack.setNormalisedProgress(it)
        })

        Children(
            modifier = Modifier.padding(
                horizontal = 64.dp,
                vertical = 12.dp
            ),
            frameModel = backStack.frames.collectAsState(listOf()),
            onElementSizeChanged = { elementSize = it }
        )
    }
}

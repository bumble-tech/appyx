package com.bumble.appyx.interactions.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.android.SampleChildren
import com.bumble.appyx.interactions.theme.appyx_dark


@ExperimentalMaterialApi
@Composable
fun BackStackExperimentDebug(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()

    val backStack = remember {
        BackStack(
            scope = coroutineScope,
            model = BackStackModel(
                initialTargets = listOf(InteractionTarget.Child1, InteractionTarget.Child2, InteractionTarget.Child3),
                savedStateMap = null
            ),
            motionController = { BackStackFader(it) },
            isDebug = false
        )
    }

    LaunchedEffect(Unit) {
//        backStack.push(Child2)
//        backStack.replace(Child6)
//        backStack.pop()
//        backStack.pop()
//        backStack.newRoot(Child1)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(appyx_dark)
    ) {
//        KnobControl(onValueChange = {
//            backStack.setNormalisedProgress(it)
//        })
        Button(onClick = {
            backStack.pop(Operation.Mode.IMMEDIATE)
        }
        ) {
            Text("POP")
        }

        SampleChildren(
            modifier = Modifier
                .padding(
                    horizontal = 64.dp,
                    vertical = 12.dp
                ),
            appyxComponent = backStack,
        )
    }
}

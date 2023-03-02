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
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.sample.NavTarget.*
import com.bumble.appyx.interactions.theme.appyx_dark
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.BackstackFader
import com.bumble.appyx.transitionmodel.backstack.operation.pop


@ExperimentalMaterialApi
@Composable
fun BackStackExperimentDebug() {
    val coroutineScope = rememberCoroutineScope()

    val backStack = remember {
        BackStack(
            scope = coroutineScope,
            model = BackStackModel(
                initialTargets = listOf(Child1, Child2, Child3),
                savedStateMap = null
            ),
            interpolator = { BackstackFader(it) },
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

        Children(
            modifier = Modifier
                .padding(
                    horizontal = 64.dp,
                    vertical = 12.dp
                ),
            interactionModel = backStack,
        )
    }
}

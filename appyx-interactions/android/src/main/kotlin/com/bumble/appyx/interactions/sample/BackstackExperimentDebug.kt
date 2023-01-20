package com.bumble.appyx.interactions.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.sample.NavTarget.Child1
import com.bumble.appyx.interactions.sample.NavTarget.Child2
import com.bumble.appyx.interactions.sample.NavTarget.Child3
import com.bumble.appyx.interactions.sample.NavTarget.Child4
import com.bumble.appyx.interactions.sample.NavTarget.Child5
import com.bumble.appyx.interactions.sample.NavTarget.Child6
import com.bumble.appyx.interactions.sample.NavTarget.Child7
import com.bumble.appyx.interactions.sample.NavTarget.Child8
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
    val coroutineScope = rememberCoroutineScope()
    var index by remember { mutableStateOf(2) }

    val backStack = remember {
        BackStack(
            scope = coroutineScope,
            model = BackStackModel(
                initialTarget = Child1,
                savedStateMap = null
            ),
            interpolator = { BackStackSlider(it) },
            isDebug = false
        )
    }

//    LaunchedEffect(Unit) {
//        backStack.push(Child2)
//        backStack.push(Child3)
//        backStack.push(Child4)
//        backStack.push(Child5)
//        backStack.replace(Child6)
//        backStack.pop()
//        backStack.pop()
//        backStack.newRoot(Child1)
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appyx_dark)
    ) {
//        KnobControl(onValueChange = {
//            backStack.setNormalisedProgress(it)
//        })

        Children(
            modifier = Modifier
                .weight(0.9f)
                .padding(
                    horizontal = 64.dp,
                    vertical = 12.dp
                ),
            interactionModel = backStack,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                backStack.push(getChild(index))
                index++
            }) {
                Text("Push")
            }
            Button(onClick = {
                backStack.replace(Child7)
            }) {
                Text("Replace")
            }
            Button(onClick = { backStack.pop() }) {
                Text("Pop")
            }
            Button(onClick = { backStack.newRoot(Child8) }) {
                Text("New Root")
            }
        }
    }
}

private fun getChild(index: Int): NavTarget =
    when (index) {
        1 -> Child1
        2 -> Child2
        3 -> Child3
        4 -> Child4
        5 -> Child5
        else -> Child6
    }

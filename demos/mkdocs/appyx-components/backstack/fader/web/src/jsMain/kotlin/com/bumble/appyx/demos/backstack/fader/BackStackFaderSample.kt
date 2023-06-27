package com.bumble.appyx.demos.backstack.fader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.demos.backstack.fader.InteractionTarget.Element
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.helper.AppyxComponentSetup
import com.bumble.appyx.interactions.sample.Children
import kotlin.random.Random

sealed class InteractionTarget {
    data class Element(val idx: Int = Random.nextInt(1, 100)) : InteractionTarget() {
        override fun toString(): String =
            "Element $idx"
    }
}

@Composable
fun BackStackFaderSample(
    screenWidthPx: Int,
    screenHeightPx: Int,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val model = remember { BackStackModel<InteractionTarget>(
        initialTarget = Element(),
        savedStateMap = null
    ) }
    val backStack = remember {
        BackStack(
            scope = coroutineScope,
            model = model,
            motionController = { BackStackFader(it) },
            gestureFactory = { GestureFactory.Noop() }
        )
    }

    AppyxComponentSetup(backStack)

    Box(
        modifier = modifier,
    ) {
        ModelUi(
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            backStack = backStack,
        )
        Controls(
            backStack = backStack
        )
    }
}

val colors = listOf(
    md_pink_500,
    md_indigo_500,
    md_blue_500,
    md_light_blue_500,
    md_cyan_500,
    md_teal_500,
    md_light_green_500,
    md_lime_500,
    md_amber_500,
    md_grey_500,
    md_blue_grey_500,
)

@Composable
fun ModelUi(
    screenWidthPx: Int,
    screenHeightPx: Int,
    backStack: BackStack<InteractionTarget>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Children(
        appyxComponent = backStack,
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        modifier = modifier
    ) { elementUiModel ->
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth(0.30f)
                    .background(
                        color = when (val target = elementUiModel.element.interactionTarget) {
                            is Element -> colors.getOrElse(target.idx % colors.size) { Color.Cyan }
                        },
                        shape = RoundedCornerShape(8)
                    )
                    .then(elementUiModel.modifier)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = elementUiModel.element.interactionTarget.toString(),
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun Controls(
    backStack: BackStack<InteractionTarget>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(color_primary, shape = RoundedCornerShape(4.dp))
                    .clickable { backStack.pop() }
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Text("Pop")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .background(color_primary, shape = RoundedCornerShape(4.dp))
                    .clickable { backStack.push(Element()) }
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Text("Push")
            }
        }


    }
}

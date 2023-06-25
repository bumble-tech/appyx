package com.bumble.appyx.demos.common

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.demos.common.InteractionTarget.Element
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.sample.Children
import kotlin.random.Random

sealed class InteractionTarget {
    data class Element(val idx: Int = Random.nextInt(1, 100)) : InteractionTarget() {
        override fun toString(): String =
            "Element $idx"
    }
}

@Composable
fun AppyxWebSample(
    screenWidthPx: Int,
    screenHeightPx: Int,
    interactionModel: BaseInteractionModel<InteractionTarget, Any>,
    actions: Map<String, () -> Unit>,
    isChildMaxSize: Boolean = false,
    modifier: Modifier = Modifier,
) {
    InteractionModelSetup(interactionModel)

    Box(
        modifier = modifier,
    ) {
        ModelUi(
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            interactionModel = interactionModel,
            isMaxSize = isChildMaxSize,
        )
        Controls(actions = actions)
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
    interactionModel: BaseInteractionModel<InteractionTarget, Any>,
    isMaxSize: Boolean,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Children(
        interactionModel = interactionModel,
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        modifier = modifier
    ) { elementUiModel ->
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxHeight(0.8f)
                    .fillMaxWidth(if (!isMaxSize) 0.3f else 1f)
                    .then(elementUiModel.modifier)
                    .background(
                        color = when (val target = elementUiModel.element.interactionTarget) {
                            is Element -> colors.getOrElse(target.idx % colors.size) { Color.Cyan }
                        },
                        shape = RoundedCornerShape(if (!isMaxSize) 8 else 0)
                    )
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
    actions: Map<String, () -> Unit>,
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
            actions.keys.forEachIndexed { index, actionText ->
                Box(
                    modifier = Modifier
                        .background(color_primary, shape = RoundedCornerShape(4.dp))
                        .clickable { actions.getValue(actionText).invoke() }
                        .padding(horizontal = 18.dp, vertical = 9.dp)
                ) {
                    Text(actionText)
                }
                if (index != 0 || index != actions.size - 1) {
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}

package com.bumble.appyx.demos.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.bumble.appyx.interactions.core.DraggableChildren
import com.bumble.appyx.interactions.core.model.BaseInteractionModel
import com.bumble.appyx.interactions.core.ui.helper.InteractionModelSetup
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
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

    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally,
    ) {
        DraggableChildren(
            interactionModel = interactionModel,
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            modifier = Modifier.weight(0.9f)
        ) { elementUiModel ->
            ModalUi(elementUiModel = elementUiModel, isChildMaxSize = isChildMaxSize)
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f),
        ) {
            actions.forEach { entry ->
                Action(text = entry.key, action = entry.value)
            }
        }
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
fun ModalUi(
    elementUiModel: ElementUiModel<InteractionTarget>,
    isChildMaxSize: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight(0.8f)
            .fillMaxWidth()
            .then(elementUiModel.modifier)
            .background(
                color = when (val target = elementUiModel.element.interactionTarget) {
                    is Element -> colors.getOrElse(target.idx % colors.size) { Color.Cyan }
                },
                shape = RoundedCornerShape(if (!isChildMaxSize) 8 else 0)
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

@Composable
private fun Action(
    text: String,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color_primary, shape = RoundedCornerShape(4.dp))
            .clickable { action.invoke() }
            .padding(horizontal = 18.dp, vertical = 9.dp)
    ) {
        Text(text)
    }
}

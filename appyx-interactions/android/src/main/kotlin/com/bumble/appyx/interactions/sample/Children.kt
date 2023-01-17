package com.bumble.appyx.interactions.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.interactions.core.ui.FrameModel


@ExperimentalMaterialApi
@Composable
fun <NavTarget, NavState> Children(
    frameModel: State<List<FrameModel<NavTarget, NavState>>>,
    modifier: Modifier = Modifier,
    element: @Composable (FrameModel<NavTarget, NavState>) -> Unit = {
        Element(frameModel = it)
    },
    onElementSizeChanged: (IntSize) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged(onElementSizeChanged)
    ) {
        frameModel.value.forEach {
            key(it.navElement.key) {
                element.invoke(it)
            }
        }
    }
}

@Composable
fun Element(
    color: Color = Color.Unspecified,
    frameModel: FrameModel<*, *>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val backgroundColor = remember {
        if (color == Color.Unspecified) colors.shuffled().random() else color
    }

    Box(
        modifier = Modifier
            .then(frameModel.modifier)
            .clip(RoundedCornerShape(5))
            .background(backgroundColor)
            .then(modifier)
            .padding(24.dp)
    ) {
        Text(
            text = frameModel.navElement.key.navTarget.toString(),
            fontSize = 21.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

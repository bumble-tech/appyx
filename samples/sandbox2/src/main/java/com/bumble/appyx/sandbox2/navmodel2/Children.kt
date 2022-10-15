package com.bumble.appyx.sandbox2.navmodel2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.core.navigation2.ui.Modifiers


@ExperimentalMaterialApi
@Composable
fun Children(
    render: State<List<Modifiers<*, *>>>,
    modifier: Modifier = Modifier,
    element: @Composable (Modifiers<*, *>) -> Unit = {
        Element(render = it)
    },
    onElementSizeChanged: (IntSize) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 64.dp,
                vertical = 12.dp
            )
            .onSizeChanged(onElementSizeChanged)
    ) {
        render.value.forEach {
            element.invoke(it)
        }
    }
}

@Composable
fun Element(
    color: Color = Color.Unspecified,
    render: Modifiers<*, *>,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val backgroundColor = remember {
        if (color == Color.Unspecified) colors.shuffled().random() else color
    }

    Box(
        modifier = Modifier
            .then(render.modifier)
            .clip(RoundedCornerShape(5))
            .background(backgroundColor)
            .then(modifier)
            .padding(24.dp)
    ) {
        Text(
            text = render.navElement.key.navTarget.toString(),
            fontSize = 21.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

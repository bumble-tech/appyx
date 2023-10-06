package com.bumble.appyx.navigation.node.cakes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node

class CakeDetailsNode(
    buildContext: BuildContext,
) : Node(
    buildContext = buildContext,
) {

    @Composable
    override fun View(modifier: Modifier) {
        val backgroundColorIdx = rememberSaveable { colors.shuffled().indices.random() }
        val backgroundColor = colors[backgroundColorIdx]

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5))
                .background(backgroundColor)
                .padding(24.dp)

        ) {
            Text(
                text = "Cake",
                fontSize = 21.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

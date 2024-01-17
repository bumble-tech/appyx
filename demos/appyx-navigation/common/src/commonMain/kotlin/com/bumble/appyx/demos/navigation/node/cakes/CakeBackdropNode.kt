package com.bumble.appyx.demos.navigation.node.cakes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumble.appyx.demos.navigation.component.spotlighthero.visualisation.property.HeroProgress
import com.bumble.appyx.interactions.core.ui.math.smoothstep
import com.bumble.appyx.interactions.core.ui.property.motionPropertyRenderValue
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node

class CakeBackdropNode(
    nodeContext: NodeContext,
    private val cake: Cake,
    private val onClick: () -> Unit,
) : Node(
    nodeContext = nodeContext,
) {

    @Composable
    override fun Content(modifier: Modifier) {
        val heroProgress = motionPropertyRenderValue<Float, HeroProgress>() ?: 0f

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(cake.backgroundColor))
                .clickable { onClick() }
                .padding(24.dp)
        ) {
            Text(
                text = cake.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .alpha(1f - smoothstep(0f, 0.15f, heroProgress))
            )
        }
    }
}

package com.bumble.appyx.navigation.node.cakes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.interactions.core.ui.math.smoothstep
import com.bumble.appyx.interactions.core.ui.property.motionPropertyRenderValue
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.property.HeroProgress
import com.bumble.appyx.navigation.node.cakes.model.Cake

class CakeBackdropNode(
    buildContext: BuildContext,
    private val cake: Cake,
    private val onClick: () -> Unit,
) : Node(
    buildContext = buildContext,
) {

    @Composable
    override fun View(modifier: Modifier) {
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
                fontSize = 21.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .alpha(1f - smoothstep(0f, 0.15f, heroProgress))
            )
        }
    }
}

package com.bumble.appyx.navigation.node.cakes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bumble.appyx.imageloader.ResourceImage
import com.bumble.appyx.interactions.core.ui.property.motionPropertyRenderValue
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.cakes.component.spotlighthero.visualisation.property.HeroProgress
import com.bumble.appyx.navigation.node.cakes.model.Cake

class CakeImageNode(
    buildContext: BuildContext,
    private val cake: Cake,
    private val onClick: () -> Unit,
) : Node(
    buildContext = buildContext,
) {

    @Composable
    override fun View(modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val heroProgress = motionPropertyRenderValue<Float, HeroProgress>() ?: 0f

        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(24.dp)
                .zIndex(10f)
                .clickable(
                    onClick = { onClick() },
                    indication = null,
                    interactionSource = interactionSource
                ),
            contentAlignment = Alignment.Center
        ) {
            ResourceImage(
                path = cake.image,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
                contentDescription = cake.name
            )
        }
    }
}

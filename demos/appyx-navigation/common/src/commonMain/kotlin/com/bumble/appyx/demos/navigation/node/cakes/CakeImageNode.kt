package com.bumble.appyx.demos.navigation.node.cakes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
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
import com.bumble.appyx.demos.navigation.ui.EmbeddableResourceImage
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode

class CakeImageNode(
    nodeContext: NodeContext,
    private val cake: Cake,
    private val onClick: () -> Unit,
) : LeafNode(
    nodeContext = nodeContext,
) {

    @Composable
    override fun Content(modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp)
                .zIndex(10f)
                .clickable(
                    onClick = { onClick() },
                    indication = null,
                    interactionSource = interactionSource
                ),
            contentAlignment = Alignment.Center
        ) {
            EmbeddableResourceImage(
                path = cake.image,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.FillWidth,
                contentDescription = cake.name
            )
        }
    }
}

package com.bumble.appyx.navigation.node.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.main.MainNode.InteractionTarget
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class MainNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<InteractionTarget> =
        Spotlight(
            model = SpotlightModel(
                items = List(7) { InteractionTarget.MainChild },
                initialActiveIndex = 0f,
                savedStateMap = buildContext.savedStateMap
            ),
            motionController = { SpotlightSlider(it) },
            gestureFactory = { SpotlightSlider.Gestures(it) }
        )
) : ParentNode<InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = spotlight
) {
    sealed class InteractionTarget : Parcelable {
        @Parcelize
        object MainChild : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.MainChild -> node(buildContext) {
                CakeUi()
            }
        }

    @Composable
    private fun CakeUi() {
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

    @Composable
    override fun View(modifier: Modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            AppyxComponent(
                appyxComponent = spotlight,
                modifier = Modifier
                    .fillMaxSize(0.6f)
                    .padding(top = 64.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}

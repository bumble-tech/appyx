package com.bumble.appyx.navigation.node.spotlight

import android.os.Parcelable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.first
import com.bumble.appyx.components.spotlight.operation.last
import com.bumble.appyx.components.spotlight.operation.next
import com.bumble.appyx.components.spotlight.operation.previous
import com.bumble.appyx.components.spotlight.operation.updateElements
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.node.spotlight.SpotlightNode.InteractionTarget
import com.bumble.appyx.navigation.ui.appyx_dark
import kotlinx.parcelize.Parcelize

class SpotlightNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<InteractionTarget> = Spotlight(
        model = SpotlightModel(
            items = List(7) { InteractionTarget.Child(it) },
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
    private val newItems = List(7) { InteractionTarget.Child(it * 3) }

    sealed class InteractionTarget : Parcelable {
        @Parcelize
        class Child(val index: Int) : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.Child -> node(buildContext) { modifier ->
                val backgroundColor = remember { colors.shuffled().random() }
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(5))
                        .background(backgroundColor)
                        .padding(24.dp)
                ) {
                    Text(
                        text = interactionTarget.index.toString(),
                        fontSize = 21.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(appyx_dark)
        ) {
            AppyxComponent(
                appyxComponent = spotlight,
                modifier = Modifier
                    .padding(
                        horizontal = 64.dp,
                        vertical = 12.dp
                    )
                    .weight(0.9f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    spotlight.updateElements(
                        newItems.shuffled(),
                        animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 20)
                    )
                }) {
                    Text("New")
                }
                Button(onClick = { spotlight.first() }) {
                    Text("First")
                }
                Button(onClick = { spotlight.previous(spring(stiffness = Spring.StiffnessLow)) }) {
                    Text("Prev")
                }
                Button(onClick = { spotlight.next(spring(stiffness = Spring.StiffnessMedium)) }) {
                    Text("Next")
                }
                Button(onClick = { spotlight.last() }) {
                    Text("Last")
                }
            }
        }
    }
}

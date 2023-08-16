package com.bumble.appyx.navigation.node.spotlight

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
import androidx.compose.ui.Alignment
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
import com.bumble.appyx.components.spotlight.ui.sliderrotation.SpotlightSliderRotation
import com.bumble.appyx.interactions.core.ui.property.impl.RotationY
import com.bumble.appyx.interactions.core.ui.property.impl.position.PositionOutside
import com.bumble.appyx.interactions.core.ui.property.motionPropertyRenderValue
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.node.spotlight.SpotlightObserveTransitionsExampleNode.InteractionTarget
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class SpotlightObserveTransitionsExampleNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<InteractionTarget> = Spotlight(
        model = SpotlightModel(
            items = List(7) { InteractionTarget.Child(it) },
            initialActiveIndex = 0f,
            savedStateMap = buildContext.savedStateMap
        ),
        motionController = { SpotlightSliderRotation(it) },
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

                    Column(modifier = Modifier.align(Alignment.Center)) {

                        val alignment = motionPropertyRenderValue<PositionOutside.Value, PositionOutside>()?.alignment
                        if (alignment != null) {
                            Text(
                                text = "Screens offset: ${alignment.horizontalBias.toTwoPointPrecisionString()}",
                                fontSize = 20.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val rotationY = motionPropertyRenderValue<Float, RotationY>()
                        if (rotationY != null) {
                            Text(
                                text = "RotationY: ${rotationY.toTwoPointPrecisionString()}°",
                                fontSize = 20.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
                Button(onClick = { spotlight.updateElements(newItems.shuffled()) }) {
                    Text("New")
                }
                Button(onClick = { spotlight.first() }) {
                    Text("First")
                }
                Button(onClick = { spotlight.previous() }) {
                    Text("Prev")
                }
                Button(onClick = { spotlight.next() }) {
                    Text("Next")
                }
                Button(onClick = { spotlight.last() }) {
                    Text("Last")
                }
            }
        }
    }

    /**
     * Format a float to round down to a consistent 2 decimal places for display
     */
    private fun Float.toTwoPointPrecisionString(): String =
        ((this * 100.0).toInt() / 100.0).toString().let {
            val dpIndex = it.indexOf(".")
            if (dpIndex >= 0) {
                it.padEnd(dpIndex + 3, "0".first())
            } else {
                it
            }
        }
}

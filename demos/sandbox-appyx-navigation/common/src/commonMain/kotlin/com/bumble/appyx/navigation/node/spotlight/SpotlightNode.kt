package com.bumble.appyx.navigation.node.spotlight

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.bumble.appyx.navigation.composable.AppyxNavigationComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.node.spotlight.SpotlightNode.NavTarget
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class SpotlightNode(
    buildContext: BuildContext,
    private val model: SpotlightModel<NavTarget> = SpotlightModel(
        items = List(7) { NavTarget.Child(it) },
        initialActiveIndex = 0f,
        savedStateMap = buildContext.savedStateMap,
    ),
    private val spotlight: Spotlight<NavTarget> = Spotlight(
        model = model,
        visualisation = { SpotlightSlider(it, model.currentState) },
        gestureFactory = { SpotlightSlider.Gestures(it) }
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = spotlight
) {
    private val newItems = List(7) { NavTarget.Child(it * 3) }

    sealed class NavTarget : Parcelable {
        @Parcelize
        class Child(val index: Int) : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Child -> node(buildContext) { modifier ->
                val backgroundColorIdx = rememberSaveable { colors.shuffled().indices.random() }
                val backgroundColor = colors[backgroundColorIdx]
                var clicked by rememberSaveable { mutableStateOf(false) }

                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(5))
                        .background(backgroundColor)
                        .clickable { clicked = true }
                        .padding(24.dp)

                ) {
                    Text(
                        text = "${navTarget.index} – Clicked: $clicked",
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
            AppyxNavigationComponent(
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

package com.bumble.appyx.navigation.node.promoter

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.experimental.promoter.Promoter
import com.bumble.appyx.components.experimental.promoter.PromoterModel
import com.bumble.appyx.components.experimental.promoter.operation.addFirst
import com.bumble.appyx.components.experimental.promoter.ui.PromoterVisualisation
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.IMMEDIATE
import com.bumble.appyx.interactions.core.model.transition.Operation.Mode.KEYFRAME
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.composable.AppyxNavigationComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.node.promoter.PromoterNode.InteractionTarget
import com.bumble.appyx.navigation.node.promoter.PromoterNode.InteractionTarget.Child
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class PromoterNode(
    buildContext: BuildContext,
    private val promoter: Promoter<InteractionTarget> = Promoter(
        model = PromoterModel(
            savedStateMap = buildContext.savedStateMap
        ),
        visualisation = {
            PromoterVisualisation(
                uiContext = it
            )
        },
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow / 20)
    )
) : ParentNode<InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = promoter
) {

    init {
        promoter.addFirst(Child(1))
        promoter.addFirst(Child(2))
        promoter.addFirst(Child(3))
        promoter.addFirst(Child(4))
    }

    sealed class InteractionTarget : Parcelable {
        @Parcelize
        class Child(val index: Int) : InteractionTarget()
    }

    override fun buildChildNode(navTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is Child -> node(buildContext) {
                val backgroundColor = remember { colors.shuffled().random() }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(5))
                        .background(backgroundColor)
                        .padding(24.dp)
                ) {
                    Text(
                        text = navTarget.index.toString(),
                        fontSize = 21.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        var index by remember { mutableStateOf(5) }

        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            AppyxNavigationComponent(
                appyxComponent = promoter,
                modifier = Modifier
                    .weight(0.9f)
                    .padding(
                        horizontal = 64.dp,
                        vertical = 12.dp
                    ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { promoter.addFirst(Child(index++), KEYFRAME) }
                ) {
                    Text("KEYFRAME")
                }
                Spacer(Modifier.size(24.dp))
                Button(
                    onClick = { promoter.addFirst(Child(index++), IMMEDIATE) }
                ) {
                    Text("IMMEDIATE")
                }
            }
        }

    }
}

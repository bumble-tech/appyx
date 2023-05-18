package com.bumble.appyx.navigation.node.permanentchild

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.interactions.permanent.PermanentInteractionModel
import com.bumble.appyx.interactions.permanent.PermanentModel
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.ui.appyx_dark
import kotlinx.parcelize.Parcelize


class PermanentChildNode(
    buildContext: BuildContext,
) : ParentNode<PermanentChildNode.InteractionTarget>(
    buildContext = buildContext,
    interactionModel = PermanentInteractionModel(model = PermanentModel(buildContext.savedStateMap))
) {
    sealed class InteractionTarget : Parcelable {
        @Parcelize
        object Child1 : InteractionTarget()

        @Parcelize
        object Child2 : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.Child1 -> node(buildContext) {
                val backgroundColor = remember { colors.shuffled().random() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(200.dp)
                        .clip(RoundedCornerShape(5))
                        .background(backgroundColor)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Permanent Child One",
                        fontSize = 21.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            is InteractionTarget.Child2 -> node(buildContext) {
                val backgroundColor = remember { colors.shuffled().random() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(200.dp)
                        .clip(RoundedCornerShape(5))
                        .background(backgroundColor)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Permanent Child Two",
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
                .fillMaxWidth()
                .background(appyx_dark)
        ) {
            PermanentChild(interactionTarget = InteractionTarget.Child1)
            PermanentChild(interactionTarget = InteractionTarget.Child2)
        }
    }
}


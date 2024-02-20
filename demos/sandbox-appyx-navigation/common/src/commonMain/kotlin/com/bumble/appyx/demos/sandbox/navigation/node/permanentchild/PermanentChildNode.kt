package com.bumble.appyx.demos.sandbox.navigation.node.permanentchild

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
import com.bumble.appyx.demos.sandbox.navigation.colors
import com.bumble.appyx.demos.sandbox.navigation.ui.appyx_dark
import com.bumble.appyx.interactions.permanent.PermanentAppyxComponent
import com.bumble.appyx.navigation.composable.PermanentChild
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class PermanentChildNode(
    nodeContext: NodeContext,
    private val permanentAppyxComponent: PermanentAppyxComponent<NavTarget> =
        PermanentAppyxComponent(
            savedStateMap = nodeContext.savedStateMap,
            initialTargets = listOf(
                NavTarget.Child1,
                NavTarget.Child2
            )
        )
) : Node<PermanentChildNode.NavTarget>(
    nodeContext = nodeContext,
    appyxComponent = permanentAppyxComponent
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object Child1 : NavTarget()

        @Parcelize
        object Child2 : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): Node<*> =
        when (navTarget) {
            is NavTarget.Child1 -> node(nodeContext) {
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

            is NavTarget.Child2 -> node(nodeContext) {
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
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(appyx_dark)
        ) {
            PermanentChild(
                permanentAppyxComponent = permanentAppyxComponent,
                navTarget = NavTarget.Child1
            )
            PermanentChild(
                permanentAppyxComponent = permanentAppyxComponent,
                navTarget = NavTarget.Child2
            )
        }
    }
}


package com.bumble.appyx.navigation.node.backstack

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.bumble.appyx.interactions.core.ui.MotionController
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.composable.Children
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.ui.TextButton
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.operation.newRoot
import com.bumble.appyx.transitionmodel.backstack.operation.pop
import com.bumble.appyx.transitionmodel.backstack.operation.push
import com.bumble.appyx.transitionmodel.backstack.operation.replace
import kotlin.random.Random
import kotlinx.parcelize.Parcelize


class BackStackNode(
    buildContext: BuildContext,
    motionController: (UiContext) -> MotionController<NavTarget, BackStackModel.State<NavTarget>>,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.Child(1)),
            savedStateMap = buildContext.savedStateMap,
            key = BackStackNode::class.java.name
        ),
        motionController = motionController
    )
) : ParentNode<BackStackNode.NavTarget>(
    buildContext = buildContext,
    interactionModel = backStack,
    key = BackStackNode::class.java.name
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        class Child(val index: Int) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Child -> node(buildContext) {
                val backgroundColor = remember { colors.shuffled().random() }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(appyx_dark)
        ) {
            Children(
                interactionModel = backStack,
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxSize()
                    .background(appyx_dark)
                    .padding(16.dp),
            )
            Row(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(text = "Push") {
                    backStack.push(NavTarget.Child(Random.nextInt(20)))
                }
                TextButton(text = "Pop") {
                    backStack.pop()
                }
                TextButton(text = "Replace") {
                    backStack.replace(NavTarget.Child(Random.nextInt(20)))
                }
                TextButton(text = "New root") {
                    backStack.newRoot(NavTarget.Child(Random.nextInt(20)))
                }
            }
        }
    }
}


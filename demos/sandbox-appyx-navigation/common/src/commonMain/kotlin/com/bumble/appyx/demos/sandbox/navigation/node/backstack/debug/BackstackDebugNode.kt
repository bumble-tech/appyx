package com.bumble.appyx.demos.sandbox.navigation.node.backstack.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.newRoot
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.demos.sandbox.navigation.colors
import com.bumble.appyx.demos.sandbox.navigation.composable.KnobControl
import com.bumble.appyx.demos.sandbox.navigation.node.backstack.debug.BackstackDebugNode.NavTarget
import com.bumble.appyx.demos.sandbox.navigation.ui.appyx_dark
import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.AbstractNode
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class BackstackDebugNode(
    nodeContext: NodeContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.Child(1)),
            savedStateMap = nodeContext.savedStateMap,
        ),
        visualisation = { BackStackSlider(it) }
    )
) : Node<NavTarget>(
    nodeContext = nodeContext,
    appyxComponent = backStack
) {

    init {
        backStack.push(NavTarget.Child(2))
        backStack.push(NavTarget.Child(3))
        backStack.push(NavTarget.Child(4))
        backStack.push(NavTarget.Child(5))
        backStack.replace(NavTarget.Child(6))
        backStack.pop()
        backStack.pop()
        backStack.newRoot(NavTarget.Child(1))
    }

    sealed class NavTarget : Parcelable {
        @Parcelize
        class Child(val index: Int) : NavTarget()
    }

    override fun buildChildNode(navTarget: NavTarget, nodeContext: NodeContext): AbstractNode =
        when (navTarget) {
            is NavTarget.Child -> node(nodeContext) {
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

    @ExperimentalMaterialApi
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(appyx_dark)
        ) {
            KnobControl(onValueChange = {
                backStack.setNormalisedProgress(it)
            })
            AppyxNavigationContainer(
                appyxComponent = backStack,
                modifier = Modifier
                    .fillMaxSize()
                    .background(appyx_dark)
                    .padding(16.dp),
            )
        }
    }
}

package com.bumble.appyx.navigation.node.backstack.debug

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
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.composable.KnobControl
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.backstack.debug.BackstackDebugNode.InteractionTarget
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class BackstackDebugNode(
    buildContext: BuildContext,
    private val backStack: BackStack<InteractionTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(InteractionTarget.Child(1)),
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackSlider(it) }
    )
) : ParentNode<InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
) {

    init {
        backStack.push(InteractionTarget.Child(2))
        backStack.push(InteractionTarget.Child(3))
        backStack.push(InteractionTarget.Child(4))
        backStack.push(InteractionTarget.Child(5))
        backStack.replace(InteractionTarget.Child(6))
        backStack.pop()
        backStack.pop()
        backStack.newRoot(InteractionTarget.Child(1))
    }

    sealed class InteractionTarget : Parcelable {
        @Parcelize
        class Child(val index: Int) : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.Child -> node(buildContext) {
                val backgroundColor = remember { colors.shuffled().random() }

                Box(
                    modifier = Modifier
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

    @ExperimentalMaterialApi
    @Composable
    override fun View(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(appyx_dark)
        ) {
            KnobControl(onValueChange = {
                backStack.setNormalisedProgress(it)
            })
            AppyxComponent(
                appyxComponent = backStack,
                modifier = Modifier
                    .fillMaxSize()
                    .background(appyx_dark)
                    .padding(16.dp),
            )
        }
    }
}

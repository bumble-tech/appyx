package com.bumble.appyx.navigation.node.backstack

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.BackStackModel.State
import com.bumble.appyx.components.backstack.operation.newRoot
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.interactions.core.ui.Visualisation
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.gesture.GestureFactory
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig
import com.bumble.appyx.navigation.ColorSaver
import com.bumble.appyx.navigation.colors
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.ui.TextButton
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import kotlin.random.Random


class BackStackNode(
    buildContext: BuildContext,
    visualisation: (UiContext) -> Visualisation<InteractionTarget, State<InteractionTarget>>,
    gestureFactory: (TransitionBounds) -> GestureFactory<InteractionTarget, State<InteractionTarget>> = {
        GestureFactory.Noop()
    },
    gestureSettleConfig: GestureSettleConfig = GestureSettleConfig(),
    private val isMaxSize: Boolean = false,
    private val backStack: BackStack<InteractionTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(InteractionTarget.Child(1)),
            savedStateMap = buildContext.savedStateMap
        ),
        visualisation = visualisation,
        gestureFactory = gestureFactory,
        gestureSettleConfig = gestureSettleConfig,
    )
) : ParentNode<BackStackNode.InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = backStack,
) {
    sealed class InteractionTarget : Parcelable {
        @Parcelize
        class Child(val index: Int) : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.Child -> node(buildContext) {
                val backgroundColor =
                    rememberSaveable(saver = ColorSaver) { colors.shuffled().random() }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (isMaxSize) {
                                Modifier
                            } else {
                                Modifier.clip(RoundedCornerShape(5))
                            }
                        )
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
                .fillMaxWidth()
                .background(appyx_dark)
        ) {
            AppyxComponent(
                clipToBounds = true,
                appyxComponent = backStack,
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxSize()
                    .background(appyx_dark)
                    .then(
                        if (isMaxSize) {
                            Modifier.padding(bottom = 16.dp)
                        } else {
                            Modifier.padding(16.dp)
                        }
                    ),
            )
            Row(
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(text = "Push") {
                    backStack.push(InteractionTarget.Child(Random.nextInt(20)))
                }
                TextButton(text = "Pop") {
                    backStack.pop()
                }
                TextButton(text = "Replace") {
                    backStack.replace(InteractionTarget.Child(Random.nextInt(20)))
                }
                TextButton(text = "New root") {
                    backStack.newRoot(InteractionTarget.Child(Random.nextInt(20)))
                }
            }
        }
    }
}


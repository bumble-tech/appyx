package com.bumble.appyx.navigation.node.backstack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.components.backstack.ui.stack3d.BackStack3D
import com.bumble.appyx.interactions.core.ui.gesture.GestureSettleConfig
import com.bumble.appyx.navigation.composable.AppyxNavigationComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.backstack.BackStackExamplesNode.InteractionTarget
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.ui.TextButton
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize


class BackStackExamplesNode(
    buildContext: BuildContext,
    private val backStack: BackStack<InteractionTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(InteractionTarget.BackStackPicker),
            savedStateMap = buildContext.savedStateMap
        ),
        visualisation = { BackStackSlider(it) }
    )
) : ParentNode<InteractionTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
) {

    private val padding = mutableStateOf(16)

    sealed class InteractionTarget : Parcelable {
        @Parcelize
        object BackStackPicker : InteractionTarget()

        @Parcelize
        object BackStackSlider : InteractionTarget()

        @Parcelize
        object BackStackFader : InteractionTarget()

        @Parcelize
        object BackstackParallax : InteractionTarget()

        @Parcelize
        object BackStack3D : InteractionTarget()
    }

    override fun resolve(interactionTarget: InteractionTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is InteractionTarget.BackStackPicker -> node(buildContext) {
                BackStackPicker(it)
            }
            is InteractionTarget.BackStackFader -> BackStackNode(buildContext, {
                BackStackFader(
                    it
                )
            })
            is InteractionTarget.BackStackSlider -> BackStackNode(buildContext, {
                BackStackSlider(
                    it
                )
            })
            is InteractionTarget.BackstackParallax -> BackStackNode(
                buildContext = buildContext,
                visualisation = { BackStackParallax(uiContext = it) },
                gestureFactory = { BackStackParallax.Gestures(it) },
                isMaxSize = true
            ).also {
                padding.value = 0
            }
            is InteractionTarget.BackStack3D -> BackStackNode(
                buildContext = buildContext,
                visualisation = { BackStack3D(it) },
                gestureFactory = { BackStack3D.Gestures(it) },
                gestureSettleConfig = GestureSettleConfig(completionThreshold = 0.2f),
            )
        }

    @Composable
    private fun BackStackPicker(modifier: Modifier = Modifier) {
        val scrollState = rememberScrollState()
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextButton(text = "BackStack slider") {
                    backStack.push(InteractionTarget.BackStackSlider)
                }
                TextButton(text = "BackStack fader") {
                    backStack.push(InteractionTarget.BackStackFader)
                }
                TextButton(text = "BackStack parallax") {
                    backStack.push(InteractionTarget.BackstackParallax)
                }
                TextButton(text = "BackStack 3D") {
                    backStack.push(InteractionTarget.BackStack3D)
                }
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxNavigationComponent(
            appyxComponent = backStack,
            modifier = Modifier
                .fillMaxSize()
                .background(appyx_dark)
                .padding(padding.value.dp),
        )
    }
}


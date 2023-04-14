package com.bumble.appyx.navigation.node.backstack

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.node.backstack.BackStackExamplesNode.NavTarget
import com.bumble.appyx.navigation.ui.TextButton
import com.bumble.appyx.navigation.ui.appyx_dark
import com.bumble.appyx.navigation.composable.Children
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.transitionmodel.backstack.BackStack
import com.bumble.appyx.transitionmodel.backstack.BackStackModel
import com.bumble.appyx.transitionmodel.backstack.ui.fader.BackstackFader
import com.bumble.appyx.transitionmodel.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.transitionmodel.backstack.operation.push
import kotlinx.parcelize.Parcelize


class BackStackExamplesNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.BackStackPicker),
            savedStateMap = buildContext.savedStateMap
        ),
        motionController = { BackStackSlider(it) }
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    interactionModel = backStack
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        object BackStackPicker : NavTarget()

        @Parcelize
        object BackStackSlider : NavTarget()

        @Parcelize
        object BackStackFader : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.BackStackPicker -> node(buildContext) {
                BackStackPicker(it)
            }
            is NavTarget.BackStackFader -> BackStackNode(buildContext, { BackstackFader(it) })
            is NavTarget.BackStackSlider -> BackStackNode(buildContext, { BackStackSlider(it) })
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
                    backStack.push(NavTarget.BackStackSlider)
                }
                TextButton(text = "BackStack fader") {
                    backStack.push(NavTarget.BackStackFader)
                }
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            interactionModel = backStack,
            modifier = Modifier
                .fillMaxSize()
                .background(appyx_dark)
                .padding(16.dp),
        )
    }
}


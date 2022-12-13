package com.bumble.appyx.app.node.backstack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.app.node.backstack.InsideTheBackStack.NavTarget
import com.bumble.appyx.app.node.backstack.app.ChildNode
import com.bumble.appyx.app.node.backstack.app.composable.CustomButton
import com.bumble.appyx.app.node.backstack.app.composable.LogoHeader
import com.bumble.appyx.app.node.backstack.app.composable.PeekInsideBackStack
import com.bumble.appyx.app.node.backstack.app.custombackstack.CustomBackStack
import com.bumble.appyx.app.node.backstack.app.custombackstack.operation.pop
import com.bumble.appyx.app.node.backstack.app.custombackstack.operation.push
import com.bumble.appyx.app.node.backstack.app.custombackstack.transition.rememberRecentsTransitionHandler
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.spotlight.operation.next
import com.bumble.appyx.navmodel.spotlight.operation.previous
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class InsideTheBackStack(
    buildContext: BuildContext,
    autoAdvanceDelayMs: Long? = null,
    private val backStack: CustomBackStack<NavTarget> = CustomBackStack(
        savedState = buildContext.savedStateMap,
        initialElement = NavTarget.Child(0)
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    navModel = backStack
) {

    init {
        autoAdvanceDelayMs?.let { ms ->
            lifecycle.coroutineScope.launchWhenStarted {
                while (isActive) {
                    delay(ms)
                    repeat(4) {
                        backStack.push(NavTarget.Child(it + 1))
                        delay(ms)
                    }
                    repeat(4) {
                        backStack.pop()
                        delay(ms)
                    }
                }
            }
        }
    }

    sealed class NavTarget {
        data class Child(val index: Int) : NavTarget() {
            override fun toString(): String = index.toString()
        }
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Child -> ChildNode(buildContext, navTarget.index)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                var isFader by remember { mutableStateOf(false) }

                LogoHeader(Modifier.weight(0.1f)) {
                    isFader = !isFader
                }
                PeekInsideBackStack(backStack, Modifier.weight(0.1f))
                Spacer(Modifier.weight(0.1f))
                BackStackContent(
                    modifier = Modifier.weight(0.7f)
                )
            }

            Controls(Modifier.align(Alignment.BottomCenter))
        }

    }

    @Composable
    private fun BackStackContent(
        modifier: Modifier = Modifier,
    ) {
        Children(
            modifier = modifier.padding(16.dp),
            navModel = backStack,
            transitionHandler = rememberRecentsTransitionHandler()
        )
    }

    @Composable
    fun Controls(modifier: Modifier) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            CustomButton(onClick = { backStack.pop() }) {
                Text("Pop")
            }
            CustomButton(onClick = { backStack.push(NavTarget.Child(backStack.elements.value.size)) }) {
                Text("Push")
            }
        }
    }
}


package com.bumble.appyx.sandbox.client.combined

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.core.navigation.model.combined.plus
import com.bumble.appyx.sandbox.client.child.ChildNode
import com.bumble.appyx.sandbox.client.combined.CombinedNavModelNode.NavTarget.Dynamic.Child
import kotlinx.parcelize.Parcelize
import java.util.UUID

class CombinedNavModelNode(
    buildContext: BuildContext,
    private val backStack1: BackStack<NavTarget> = BackStack(
        initialElement = Child(UUID.randomUUID().toString()),
        savedStateMap = buildContext.savedStateMap,
        key = "BackStack1",
    ),
    private val backStack2: BackStack<NavTarget> = BackStack(
        initialElement = Child(UUID.randomUUID().toString()),
        savedStateMap = buildContext.savedStateMap,
        key = "BackStack2",
    ),
) : ParentNode<CombinedNavModelNode.NavTarget>(
    buildContext = buildContext,
    navModel = backStack1 + backStack2,
) {

    sealed class NavTarget : Parcelable {
        sealed class Permanent : NavTarget() {
            @Parcelize
            object Child1 : Permanent()
        }

        sealed class Dynamic : NavTarget() {
            @Parcelize
            data class Child(val id: String) : Dynamic()
        }
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Dynamic.Child -> ChildNode(navTarget.id, buildContext)
            is NavTarget.Permanent.Child1 -> ChildNode("Permanent", buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {

            Permanent()

            Spacer(modifier = Modifier.height(16.dp))

            BackStack("BackStack1", backStack1)

            Spacer(modifier = Modifier.height(16.dp))

            BackStack("BackStack2", backStack2)

        }
    }

    @Composable
    private fun Permanent(modifier: Modifier = Modifier) {
        var visibility by remember { mutableStateOf(true) }
        Column(modifier = modifier) {
            Text(text = "Permanent")
            Button(onClick = { visibility = !visibility }) {
                Text(text = "Trigger visibility")
            }
            PermanentChild(NavTarget.Permanent.Child1) { child ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    this@Column.AnimatedVisibility(visible = visibility) {
                        child()
                    }
                }
            }
        }
    }

    @Composable
    private fun BackStack(
        name: String,
        backStack: BackStack<NavTarget>,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier) {
            Text(text = name)
            Children(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                navModel = backStack,
                transitionHandler = rememberBackstackFader(transitionSpec = { tween(300) }),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    backStack.push(
                        NavTarget.Dynamic.Child(
                            UUID.randomUUID().toString()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Add", modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }

}

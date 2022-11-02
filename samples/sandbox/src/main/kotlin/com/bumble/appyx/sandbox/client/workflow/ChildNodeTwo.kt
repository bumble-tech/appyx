package com.bumble.appyx.sandbox.client.workflow

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.sandbox.client.workflow.ChildNodeTwo.NavTarget
import com.bumble.appyx.sandbox.client.workflow.ChildNodeTwo.NavTarget.GrandchildOne
import com.bumble.appyx.sandbox.client.workflow.ChildNodeTwo.NavTarget.GrandchildTwo
import com.bumble.appyx.sandbox.client.workflow.treenavigator.Navigator
import kotlinx.parcelize.Parcelize

class ChildNodeTwo(
    buildContext: BuildContext,
    private val navigator: Navigator,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = GrandchildOne,
        savedStateMap = buildContext.savedStateMap
    ),
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    navModel = backStack,
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        object GrandchildOne : NavTarget()

        @Parcelize
        object GrandchildTwo : NavTarget()
    }

    suspend fun attachGrandchildTwo(): GrandchildNodeTwo {
        return attachWorkflow {
            backStack.push(GrandchildTwo)
        }
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext) =
        when (navTarget) {
            is GrandchildOne -> GrandchildNodeOne(buildContext, navigator)
            is GrandchildTwo -> GrandchildNodeTwo(buildContext, navigator)
        }


    @Composable
    override fun View(modifier: Modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.DarkGray)
        ) {
            Text(text = "Child two")
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { backStack.push(GrandchildOne) }) {
                    Text(text = "Push one")
                }
                Button(onClick = { backStack.push(GrandchildTwo) }) {
                    Text(text = "Push two")
                }
                Button(onClick = { backStack.pop() }) {
                    Text(text = "Pop")
                }
            }
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Children(navModel = backStack)
        }
    }

}

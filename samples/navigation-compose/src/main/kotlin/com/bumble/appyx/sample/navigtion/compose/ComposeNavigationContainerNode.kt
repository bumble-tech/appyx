package com.bumble.appyx.sample.navigtion.compose

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import kotlinx.parcelize.Parcelize

internal class ComposeNavigationContainerNode(
    buildContext: BuildContext,
    private val onGoogleNavigationClick: () -> Unit,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = NavTarget.Main,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<ComposeNavigationContainerNode.NavTarget>(
    navModel = backStack,
    buildContext = buildContext,
) {

    sealed class NavTarget : Parcelable {
        @Parcelize
        object Main : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Main -> node(buildContext) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Appyx screen")
                    Button(onClick = { onGoogleNavigationClick() }) {
                        Text("Navigate to Google")
                    }
                }
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier.fillMaxWidth(),
            navModel = backStack
        ) {
            children<NavTarget> { child ->
                child()
            }
        }
    }
}

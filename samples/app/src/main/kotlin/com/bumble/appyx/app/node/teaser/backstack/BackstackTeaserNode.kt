package com.bumble.appyx.app.node.teaser.backstack

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.app.node.child.GenericChildNode
import com.bumble.appyx.app.node.teaser.backstack.BackstackTeaserNode.NavTarget
import com.bumble.appyx.app.node.teaser.backstack.BackstackTeaserNode.NavTarget.Child
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackSlider
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@ExperimentalUnitApi
class BackstackTeaserNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = Child(100),
        savedStateMap = buildContext.savedStateMap
    ),
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    navModel = backStack
) {

    init {
        lifecycle.coroutineScope.launchWhenCreated {
            delay(4000)
            // PUSH portal after timeout
        }
    }

    sealed class NavTarget : Parcelable {
        @Parcelize
        data class Child(val int: Int = Random.nextInt(1000), val isPortal: Boolean = false) :
            NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Child -> GenericChildNode(buildContext, navTarget.int, isPortal = navTarget.isPortal)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    backStack.push(NavTarget.Child(400, true))
                },
            navModel = backStack,
            transitionHandler = rememberBackstackSlider()
        )
    }
}


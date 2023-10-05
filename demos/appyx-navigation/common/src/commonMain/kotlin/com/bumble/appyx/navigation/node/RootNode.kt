package com.bumble.appyx.navigation.node

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.RootNode.NavTarget
import com.bumble.appyx.navigation.node.cakecategory.CakeCategoryNode
import com.bumble.appyx.navigation.node.main.MainNode
import com.bumble.appyx.navigation.node.profile.ProfileNode
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(NavTarget.Main),
            savedStateMap = buildContext.savedStateMap,
        ),
        motionController = { BackStackSlider(it) }
    )

) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object Main : NavTarget()

        @Parcelize
        object CakeCategory : NavTarget()

        @Parcelize
        object Profile : NavTarget()
    }


    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Main -> MainNode(buildContext)
            is NavTarget.CakeCategory -> CakeCategoryNode(buildContext)
            is NavTarget.Profile -> ProfileNode(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Column(modifier = modifier.fillMaxSize()) {
            AppyxComponent(
                appyxComponent = backStack,
                modifier = Modifier
                    .weight(0.9f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button({ backStack.replace(NavTarget.Main) }) { Text("Main") }
                Button({ backStack.replace(NavTarget.CakeCategory) }) { Text("Cakes") }
                Button({ backStack.replace(NavTarget.Profile) }) { Text("Profile") }
            }
        }
    }
}

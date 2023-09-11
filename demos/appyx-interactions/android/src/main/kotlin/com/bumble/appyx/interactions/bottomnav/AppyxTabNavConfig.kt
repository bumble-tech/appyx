package com.bumble.appyx.interactions.bottomnav

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.SpotlightModel.State
import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent

class AppyxTabNavConfig<NavTarget : Any>(
    private val navTargets: List<NavTarget>,
    private val resolver: (NavTarget) -> AppyxNavItemQ
) : AppyxNavConfig<NavTarget, SpotlightModel.State<NavTarget>> {

    private val spotlight = Spotlight(
        model = SpotlightModel(
            items = navTargets,
            initialActiveIndex = 0.toFloat(),
            savedStateMap = null
        ),
        motionController = { SpotlightFader(it,
            defaultAnimationSpec = spring(
                stiffness = Spring.StiffnessVeryLow
            ))
        }
    )

    override val appyxComponent: BaseAppyxComponent<NavTarget, State<NavTarget>> =
        spotlight

    override fun resolve(target: NavTarget): AppyxNavItemQ =
        resolver.invoke(target)


    @Composable
    fun NavigationBar() {
        // FIXME use spotlight's own selected index
        var selectedItem by remember { mutableStateOf(0) }

        NavigationBar {
            navTargets.forEachIndexed { index, item ->
                val appyxNavItem = resolve(item)

                NavigationBarItem(
                    icon = { appyxNavItem.icon(selectedItem == index) },
                    label = { appyxNavItem.text(selectedItem == index) },
                    selected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        spotlight.activate(selectedItem.toFloat())
                    }
                )
            }
        }
    }
}

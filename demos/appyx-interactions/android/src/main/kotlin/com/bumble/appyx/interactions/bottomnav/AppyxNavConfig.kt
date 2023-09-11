package com.bumble.appyx.interactions.bottomnav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.interactions.core.DraggableAppyxComponent
import com.bumble.appyx.interactions.core.gesture.GestureValidator
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.ui.helper.AppyxComponentSetup
import kotlin.math.roundToInt

interface AppyxNavConfig<NavTarget : Any, ModelState : Any> {

    fun resolve(target: NavTarget): AppyxNavItemQ

    val appyxComponent: BaseAppyxComponent<NavTarget, ModelState>

    @Composable
    fun CurrentNavItem() {
        AppyxComponentSetup(appyxComponent = appyxComponent)
        DraggableAppyxComponent(
            appyxComponent = appyxComponent,
            screenWidthPx = (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density).roundToInt(),
            screenHeightPx = (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).roundToInt(),
            gestureValidator = GestureValidator.permissiveValidator,
        ) { elementUiModel ->
            val navItem = resolve(elementUiModel.element.interactionTarget)

            Box(
                modifier = Modifier.fillMaxSize()
                    .then(elementUiModel.modifier)
            ) {
                navItem.content()
            }
        }
    }
}

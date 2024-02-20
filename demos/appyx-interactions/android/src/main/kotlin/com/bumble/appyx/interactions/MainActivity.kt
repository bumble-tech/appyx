package com.bumble.appyx.interactions

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.bumble.appyx.interactions.bottomnav.MainNavItem
import com.bumble.appyx.interactions.utils.ui.theme.AppyxTheme
import com.bumble.appyx.interactions.utils.ui.theme.appyx_dark
import com.bumble.appyx.navigation.integration.NodeActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import com.bumble.appyx.utils.material3.AppyxMaterial3NavNode


@ExperimentalMaterialApi
@ExperimentalUnitApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Suppress("MagicNumber")
class MainActivity : NodeActivity() {

    @Suppress("ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppyxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = appyx_dark
                ) {
                    NodeHost(
                        lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                        integrationPoint = appyxIntegrationPoint,
                    ) {
                        AppyxMaterial3NavNode(
                            nodeContext = it,
                            navTargets = MainNavItem.entries,
                            navTargetResolver = MainNavItem.resolver,
                            initialActiveElement = MainNavItem.CARDS
                        )
                    }
                }
            }
        }
    }
}



package com.bumble.appyx.navigation.node.main

import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.cakes.CakeListNode
import com.bumble.appyx.navigation.node.cakes.model.Cart
import com.bumble.appyx.navigation.node.home.HomeNode
import com.bumble.appyx.navigation.node.main.MainNavItem.CAKES
import com.bumble.appyx.navigation.node.main.MainNavItem.HOME
import com.bumble.appyx.navigation.node.main.MainNavItem.PROFILE
import com.bumble.appyx.navigation.node.profile.ProfileNode
import com.bumble.appyx.utils.material3.AppyxMaterial3NavNode
import kotlinx.coroutines.delay

private val mainNavItems = MainNavItem.values().toList()

class MainNode(
    buildContext: BuildContext,
    plugins: List<Plugin> = listOf(),
) : AppyxMaterial3NavNode<MainNavItem>(
    buildContext = buildContext,
    navTargets = mainNavItems,
    navTargetResolver = MainNavItem.resolver(Cart()),
    initialActiveElement = CAKES,
    plugins = plugins
) {

    suspend fun goToCakes(delay: Long = 0): CakeListNode {
        spotlight.activate(mainNavItems.indexOf(CAKES).toFloat())
        delay(delay)
        return attachChild {}
    }

    suspend fun onCakes(): CakeListNode {
        return attachChild {}
    }

    suspend fun goToHome(): HomeNode = attachChild {
        spotlight.activate(mainNavItems.indexOf(HOME).toFloat())
    }

    suspend fun goToProfile(): ProfileNode = attachChild {
        spotlight.activate(mainNavItems.indexOf(PROFILE).toFloat())
    }
}

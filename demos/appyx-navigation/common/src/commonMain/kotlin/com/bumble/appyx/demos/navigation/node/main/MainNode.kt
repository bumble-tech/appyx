package com.bumble.appyx.demos.navigation.node.main

import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.demos.navigation.node.cakes.CakeListNode
import com.bumble.appyx.demos.navigation.node.cart.Cart
import com.bumble.appyx.demos.navigation.node.home.HomeNode
import com.bumble.appyx.demos.navigation.node.main.MainNavItem.CAKES
import com.bumble.appyx.demos.navigation.node.main.MainNavItem.HOME
import com.bumble.appyx.demos.navigation.node.main.MainNavItem.PROFILE
import com.bumble.appyx.demos.navigation.node.profile.ProfileNode
import com.bumble.appyx.demos.navigation.node.profile.User
import com.bumble.appyx.interactions.plugin.Plugin
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.store.getRetainedInstance
import com.bumble.appyx.utils.material3.AppyxMaterial3NavNode
import kotlinx.coroutines.delay

private val mainNavItems = MainNavItem.values().toList()

class MainNode(
    nodeContext: NodeContext,
    private val user: User,
    onLogout: () -> Unit,
    plugins: List<Plugin> = listOf(),
) : AppyxMaterial3NavNode<MainNavItem>(
    nodeContext = nodeContext,
    navTargets = mainNavItems,
    navTargetResolver = MainNavItem.resolver(
        user = user,
        cart = nodeContext.getRetainedInstance(
            key = "cart",
            factory = { Cart() },
        ),
        onLogout = onLogout
    ),
    initialActiveElement = CAKES,
    plugins = plugins
) {

    suspend fun goToCakes(delay: Long = 0): CakeListNode {
        spotlight.activate(mainNavItems.indexOf(CAKES).toFloat())
        delay(delay)
        return attachChild {}
    }

    suspend fun onCakes(): CakeListNode =
        waitForChildAttached()

    suspend fun goToHome(): HomeNode = attachChild {
        spotlight.activate(mainNavItems.indexOf(HOME).toFloat())
    }

    suspend fun goToProfile(): ProfileNode = attachChild {
        spotlight.activate(mainNavItems.indexOf(PROFILE).toFloat())
    }
}

package com.bumble.appyx.navigation.navigator

import androidx.compose.runtime.compositionLocalOf
import com.bumble.appyx.navigation.node.cakes.Cake
import com.bumble.appyx.navigation.node.profile.User
import com.bumble.appyx.navigation.node.root.RootNode
import com.bumble.appyx.navigation.plugin.NodeReadyObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Suppress("CompositionLocalAllowlist")
val LocalNavigator = compositionLocalOf { Navigator() }

class Navigator : NodeReadyObserver<RootNode> {

    private lateinit var rootNode: RootNode
    private lateinit var lifecycleScope: CoroutineScope

    override fun init(node: RootNode) {
        rootNode = node
        lifecycleScope = node.lifecycleScope
    }

    /**
     * For demonstration purposes only.
     */
    fun goToARandomCakeWithDummyUser() {
        rootNode.onLogin(User.Dummy)
        goToARandomCake()
    }

    fun goToARandomCake() {
        lifecycleScope.launch {
            rootNode
                .waitForMainAttached()
                .goToCakes(delay = 500)
                .leaveHeroMode(delay = 500)
                .goToRandomOtherCake(delay = 500)
                .enterHeroMode()
        }
    }

    fun goToCakes() {
        lifecycleScope.launch {
            rootNode
                .waitForMainAttached()
                .goToCakes(delay = 500)
        }
    }

    fun goToCake(cake: Cake) {
        lifecycleScope.launch {
            val main = rootNode.waitForMainAttached()
            main
                .onCakes()
                .goToCake(cake)
                .enterHeroMode(delay = 500)
            main
                .goToCakes()
        }
    }
}

package com.bumble.appyx.navigation.navigator

import androidx.compose.runtime.compositionLocalOf
import com.bumble.appyx.navigation.node.cakes.model.Cake
import com.bumble.appyx.navigation.node.root.RootNode
import com.bumble.appyx.navigation.plugin.NodeReadyObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val LocalNavigator = compositionLocalOf { Navigator() }

class Navigator : NodeReadyObserver<RootNode> {

    private lateinit var rootNode: RootNode
    private lateinit var lifecycleScope: CoroutineScope

    override fun init(node: RootNode) {
        rootNode = node
        lifecycleScope = node.lifecycleScope
    }

    fun goToARandomCake() {
        lifecycleScope.launch {
            rootNode
                .goToMain()
                .goToCakes(delay = 500)
                .leaveHeroMode(delay = 500)
                .goToRandomOtherCake(delay = 500)
                .enterHeroMode()
        }
    }

    fun goToCakes() {
        lifecycleScope.launch {
            rootNode
                .goToMain()
                .goToCakes(delay = 500)
        }
    }

    fun goToCake(cake: Cake) {
        lifecycleScope.launch {
            val main = rootNode.goToMain()
            main
                .onCakes()
                .goToCake(cake)
                .enterHeroMode(delay = 500)
            main
                .goToCakes()
        }
    }
}

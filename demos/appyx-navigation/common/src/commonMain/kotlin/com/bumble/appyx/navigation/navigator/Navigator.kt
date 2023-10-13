package com.bumble.appyx.navigation.navigator

import androidx.compose.runtime.compositionLocalOf
import com.bumble.appyx.navigation.node.cakes.model.Cake
import com.bumble.appyx.navigation.node.main.MainNode
import com.bumble.appyx.navigation.plugin.NodeReadyObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val LocalNavigator = compositionLocalOf { Navigator() }

class Navigator : NodeReadyObserver<MainNode> {

    private lateinit var mainNode: MainNode
    private lateinit var lifecycleScope: CoroutineScope

    override fun init(node: MainNode) {
        mainNode = node
        lifecycleScope = node.lifecycleScope
    }

    fun goToARandomCake() {
        lifecycleScope.launch {
            mainNode
                .goToCakes(delay = 500)
                .leaveHeroMode(delay = 500)
                .goToRandomOtherCake(delay = 500)
                .enterHeroMode()
        }
    }

    fun goToCakes() {
        lifecycleScope.launch {
            mainNode
                .goToCakes(delay = 500)
        }
    }

    fun goToCake(cake: Cake) {
        lifecycleScope.launch {
            mainNode
                .onCakes()
                .goToCake(cake)
                .enterHeroMode(delay = 500)
            mainNode.goToCakes()
        }
    }
}

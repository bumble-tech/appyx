package com.bumble.appyx.navmodel.tiles.backPressHandler

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.tiles.Tiles
import com.bumble.appyx.navmodel.tiles.TilesElements
import com.bumble.appyx.navmodel.tiles.operation.DeselectAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeselectAllTiles<NavTarget : Any> :
    BaseBackPressHandlerStrategy<NavTarget, Tiles.TransitionState>() {

    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::areThereSelectedTiles)
    }

    private fun areThereSelectedTiles(elements: TilesElements<NavTarget>) =
        elements.any { it.targetState == Tiles.TransitionState.SELECTED }

    override fun onBackPressed() {
        navModel.accept(DeselectAll())
    }
}

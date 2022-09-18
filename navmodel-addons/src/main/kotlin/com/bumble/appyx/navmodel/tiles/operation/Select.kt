package com.bumble.appyx.navmodel.tiles.operation

import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.tiles.Tiles
import com.bumble.appyx.navmodel.tiles.TilesElements
import kotlinx.parcelize.Parcelize

@Parcelize
data class Select<T : Any>(
    private val key: NavKey<T>
) : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>
    ): NavElements<T, Tiles.TransitionState> =
        elements.map {
            if (it.key == key && it.targetState == Tiles.TransitionState.STANDARD) {
                it.transitionTo(
                    newTargetState = Tiles.TransitionState.SELECTED,
                    operation = this
                )
            } else {
                it
            }
        }
}

fun <T : Any> Tiles<T>.select(key: NavKey<T>) {
    accept(Select(key))
}

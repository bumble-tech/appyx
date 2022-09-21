package com.bumble.appyx.navmodel.tiles.operation

import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.tiles.Tiles
import com.bumble.appyx.navmodel.tiles.TilesElements
import kotlinx.parcelize.Parcelize

@Parcelize
data class ToggleSelection<T : Any>(
    private val key: NavKey<T>
) : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>
    ): NavElements<T, Tiles.State> =
        elements.map {
            if (it.key == key) {
                when (it.targetState) {
                    Tiles.State.SELECTED -> it.transitionTo(
                        newTargetState = Tiles.State.STANDARD,
                        operation = this
                    )
                    Tiles.State.STANDARD -> it.transitionTo(
                        newTargetState = Tiles.State.SELECTED,
                        operation = this
                    )
                    else -> it
                }
            } else {
                it
            }
        }
}

fun <T : Any> Tiles<T>.toggleSelection(key: NavKey<T>) {
    accept(ToggleSelection(key))
}

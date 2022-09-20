package com.bumble.appyx.navmodel.tiles.operation

import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.tiles.Tiles
import com.bumble.appyx.navmodel.tiles.TilesElements
import kotlinx.parcelize.Parcelize

@Parcelize
class RemoveSelected<T : Any> : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>
    ): NavElements<T, Tiles.TransitionState> =
        elements.map {
            if (it.targetState == Tiles.TransitionState.SELECTED) {
                it.transitionTo(
                    newTargetState = Tiles.TransitionState.DESTROYED,
                    operation = this
                )
            } else {
                it
            }
        }

    override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

    override fun hashCode(): Int = this.javaClass.hashCode()
}

fun <T : Any> Tiles<T>.removeSelected() {
    accept(RemoveSelected())
}

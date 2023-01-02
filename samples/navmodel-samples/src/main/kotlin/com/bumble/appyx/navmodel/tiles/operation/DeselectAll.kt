package com.bumble.appyx.navmodel.tiles.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.navmodel.tiles.Tiles
import com.bumble.appyx.navmodel.tiles.Tiles.State.SELECTED
import com.bumble.appyx.navmodel.tiles.Tiles.State.STANDARD
import com.bumble.appyx.navmodel.tiles.TilesElements
import kotlinx.parcelize.Parcelize

@Parcelize
class DeselectAll<T : Parcelable> : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>
    ): NavElements<T, Tiles.State> =
        elements.transitionTo(STANDARD) {
            it.targetState == SELECTED
        }

    override fun equals(other: Any?): Boolean = this.javaClass == other?.javaClass

    override fun hashCode(): Int = this.javaClass.hashCode()
}

fun <T : Parcelable> Tiles<T>.deselectAll() {
    accept(DeselectAll())
}

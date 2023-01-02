package com.bumble.appyx.navmodel.tiles.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.tiles.Tiles
import com.bumble.appyx.navmodel.tiles.Tiles.State.SELECTED
import com.bumble.appyx.navmodel.tiles.Tiles.State.STANDARD
import com.bumble.appyx.navmodel.tiles.TilesElements
import kotlinx.parcelize.Parcelize

@Parcelize
data class Select<T : Parcelable>(
    private val key: NavKey<T>
) : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>
    ): NavElements<T, Tiles.State> =
        elements.transitionTo(SELECTED) {
            it.key == key && it.targetState == STANDARD
        }
}

fun <T : Parcelable> Tiles<T>.select(key: NavKey<T>) {
    accept(Select(key))
}

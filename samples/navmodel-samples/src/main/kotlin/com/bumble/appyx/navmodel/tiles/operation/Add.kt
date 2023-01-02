package com.bumble.appyx.navmodel.tiles.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.NavElements
import com.bumble.appyx.core.navigation.NavKey
import com.bumble.appyx.navmodel.tiles.Tiles
import com.bumble.appyx.navmodel.tiles.TilesElement
import com.bumble.appyx.navmodel.tiles.TilesElements
import com.bumble.appyx.navmodel.tiles.Tiles.State.CREATED
import com.bumble.appyx.navmodel.tiles.Tiles.State.STANDARD
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Add<T : Parcelable>(
    private val element: T
) : TilesOperation<T> {

    override fun isApplicable(elements: TilesElements<T>): Boolean = true

    override fun invoke(
        elements: TilesElements<T>,
    ): NavElements<T, Tiles.State> =
        elements + TilesElement(
            key = NavKey(element),
            fromState = CREATED,
            targetState = STANDARD,
            operation = this
        )
}

fun <T : Parcelable> Tiles<T>.add(element: T) {
    accept(Add(element))
}

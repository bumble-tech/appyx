package com.bumble.appyx.components.experimental.puzzle15

import com.bumble.appyx.components.experimental.puzzle15.Puzzle15Model.Tile
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.state.SavedStateMap
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue

class Puzzle15Model(
    savedStateMap: SavedStateMap?
) : BaseTransitionModel<Tile, Puzzle15Model.State>(
    savedStateMap = savedStateMap
) {
    enum class Tile {
        Tile1,
        Tile2,
        Tile3,
        Tile4,
        Tile5,
        Tile6,
        Tile7,
        Tile8,
        Tile9,
        Tile10,
        Tile11,
        Tile12,
        Tile13,
        Tile14,
        Tile15,
        EmptyTile,
    }

    @Parcelize
    data class State(
        val items: @RawValue List<Element<Tile>>,
        val emptyTileIndex: Int
    ) : Parcelable

    override val initialState: State
        get() {
            val items = Tile.values().toList().shuffled()
            val emptyTile = items.find { it == Tile.EmptyTile }
            val index = items.indexOf(emptyTile)
            return State(
                items = items.map { it.asElement() },
                emptyTileIndex = index
            )
        }

    override fun State.destroyedElements(): Set<Element<Tile>> = emptySet()

    override fun State.removeDestroyedElements(): State = this

    override fun State.removeDestroyedElement(element: Element<Tile>): State = this

    override fun State.availableElements(): Set<Element<Tile>> =
        this.items.toSet()

}

package com.bumble.appyx.components.demos.puzzle15

import com.bumble.appyx.components.demos.puzzle15.Puzzle15Model.Tile
import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.interactions.core.model.transition.BaseTransitionModel
import com.bumble.appyx.interactions.core.state.SavedStateMap

class Puzzle15Model(
    savedStateMap: SavedStateMap?
) : BaseTransitionModel<Tile, Puzzle15Model.State>(
    savedStateMap = savedStateMap
) {
    data class Tile(val value: String)

    @Parcelize
    data class State(
        val items: @RawValue List<Element<Tile>>,
        val emptyTileIndex: Int
    ) : Parcelable

    private fun createItems(): List<Tile> {
        val items = mutableListOf<Tile>()
        repeat(16) {
            if (it == 0) {
                items.add(Tile(""))
            } else {
                items.add(Tile(it.toString()))
            }
        }

        return items.shuffled()
    }

    override val initialState: State
        get() {
            val items = createItems()
            val emptyTile = items.find { it.value.isEmpty() }
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
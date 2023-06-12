package com.bumble.appyx.components.demos.puzzle15.operation

import com.bumble.appyx.components.demos.puzzle15.Puzzle15Model
import com.bumble.appyx.components.demos.puzzle15.Puzzle15Model.Tile
import com.bumble.appyx.interactions.Parcelable
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation

@Parcelize
class Swap(
    private val direction: Direction,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<Puzzle15Model.State>() {

    override fun createFromState(baseLineState: Puzzle15Model.State): Puzzle15Model.State =
        baseLineState

    override fun createTargetState(fromState: Puzzle15Model.State): Puzzle15Model.State {
        val newItems = fromState.swap(direction)
        val emptyTile = newItems.find { it.interactionTarget.value.isEmpty() }
        val index = newItems.indexOf(emptyTile)
        return fromState.copy(
            items = newItems,
            emptyTileIndex = index
        )
    }

    override fun isApplicable(state: Puzzle15Model.State): Boolean {
        val origin = state.emptyTileIndex
        val destination = origin + offsetForDirection(direction)
        val isNewLine =
            (destination % 4) == 0 && (origin % 4) == 3 ||
                    (destination % 4) == 3 && (origin % 4) == 0
        return (destination) in 0..15 && !isNewLine
    }

    private fun Puzzle15Model.State.swap(direction: Direction): List<Element<Tile>> {
        val origin = emptyTileIndex
        val destination = origin + offsetForDirection(direction)

        val copy = this.items.toMutableList()

        val a = copy[origin]
        copy[origin] = copy[destination]
        copy[destination] = a

        return copy
    }

    private fun offsetForDirection(direction: Direction) =
        when (direction) {
            Direction.Left -> -1
            Direction.Up -> -4
            Direction.Right -> 1
            Direction.Down -> 4
        }

    sealed class Direction : Parcelable {
        @Parcelize
        object Left : Direction()

        @Parcelize
        object Up : Direction()

        @Parcelize
        object Right : Direction()

        @Parcelize
        object Down : Direction()
    }
}

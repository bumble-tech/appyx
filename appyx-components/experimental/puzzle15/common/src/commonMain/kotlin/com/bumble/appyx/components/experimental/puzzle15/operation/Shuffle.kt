package com.bumble.appyx.components.experimental.puzzle15.operation

import com.bumble.appyx.components.experimental.puzzle15.Puzzle15Model
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation

@Parcelize
class Shuffle(
    override var mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<Puzzle15Model.State>() {

    override fun createFromState(baseLineState: Puzzle15Model.State): Puzzle15Model.State =
        baseLineState

    override fun createTargetState(fromState: Puzzle15Model.State): Puzzle15Model.State {
        val newBoard = fromState.items.shuffled()
        val emptyTile =
            newBoard.find { it.interactionTarget == Puzzle15Model.Tile.EmptyTile }
        val index = newBoard.indexOf(emptyTile)
        return fromState.copy(
            items = newBoard,
            emptyTileIndex = index
        )
    }

    override fun isApplicable(state: Puzzle15Model.State): Boolean = true
}

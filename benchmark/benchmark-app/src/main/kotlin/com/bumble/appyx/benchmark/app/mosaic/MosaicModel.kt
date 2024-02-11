package com.bumble.appyx.benchmark.app.mosaic

import com.bumble.appyx.benchmark.app.mosaic.MosaicModel.MosaicMode.SCATTERED
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.model.Elements
import com.bumble.appyx.interactions.model.asElements
import com.bumble.appyx.interactions.model.transition.BaseTransitionModel
import com.bumble.appyx.utils.multiplatform.SavedStateMap
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue

class MosaicModel(
    savedStateMap: SavedStateMap? = null,
    gridRows: Int,
    gridCols: Int,
    pieces: List<MosaicPiece>
) : BaseTransitionModel<MosaicPiece, MosaicModel.State>(
    savedStateMap = savedStateMap
) {

    enum class MosaicMode {
        SCATTERED, ASSEMBLED, FLIPPED, CAROUSEL
    }

    @Parcelize
    data class State(
        val gridRows: Int,
        val gridCols: Int,
        val mosaicMode: MosaicMode = SCATTERED,
        val pieces: @RawValue Elements<MosaicPiece> = listOf()
    ) : Parcelable {

        val maxNbElements: Int = gridRows * gridCols
    }

    override val initialState: State by lazy {
        require(pieces.all {
            it.i in 0 until gridCols && it.j in 0 until gridRows
        })

        State(
            gridRows = gridRows,
            gridCols = gridCols,
            pieces = pieces.asElements()
        )
    }

    override fun State.availableElements(): Set<Element<MosaicPiece>> =
        pieces.toSet()

    override fun State.destroyedElements(): Set<Element<MosaicPiece>> =
        emptySet()

    override fun State.removeDestroyedElement(element: Element<MosaicPiece>): State =
        this

    override fun State.removeDestroyedElements(): State =
        this
}

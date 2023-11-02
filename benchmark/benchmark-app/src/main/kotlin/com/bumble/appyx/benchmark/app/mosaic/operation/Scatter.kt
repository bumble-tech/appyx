package com.bumble.appyx.benchmark.app.mosaic.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.benchmark.app.mosaic.MosaicComponent
import com.bumble.appyx.benchmark.app.mosaic.MosaicModel
import com.bumble.appyx.benchmark.app.mosaic.MosaicModel.State
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class Scatter(
    override var mode: Operation.Mode
) : BaseOperation<State>() {

    override fun isApplicable(state: State): Boolean =
        true

    override fun createFromState(baseLineState: State): State =
        baseLineState

    override fun createTargetState(fromState: State): State =
        fromState.copy(
            mosaicMode = MosaicModel.MosaicMode.SCATTERED
        )
}

fun MosaicComponent.scatter(
    mode: Operation.Mode = Operation.Mode.IMMEDIATE,
    animationSpec: AnimationSpec<Float>? = null
) {
    operation(operation = Scatter(mode), animationSpec = animationSpec)
}

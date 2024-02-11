package com.bumble.appyx.benchmark.app.mosaic.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.benchmark.app.mosaic.MosaicComponent
import com.bumble.appyx.benchmark.app.mosaic.MosaicModel
import com.bumble.appyx.benchmark.app.mosaic.MosaicModel.State
import com.bumble.appyx.interactions.model.transition.BaseOperation
import com.bumble.appyx.interactions.model.transition.Operation
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
class Assemble(
    override var mode: Operation.Mode
) : BaseOperation<State>() {

    override fun isApplicable(state: State): Boolean =
        true

    override fun createFromState(baseLineState: State): State =
        baseLineState

    override fun createTargetState(fromState: State): State =
        fromState.copy(
            mosaicMode = MosaicModel.MosaicMode.ASSEMBLED
        )
}

fun MosaicComponent.assemble(
    mode: Operation.Mode = Operation.Mode.IMMEDIATE,
    animationSpec: AnimationSpec<Float>? = null
) {
    operation(operation = Assemble(mode), animationSpec = animationSpec)
}

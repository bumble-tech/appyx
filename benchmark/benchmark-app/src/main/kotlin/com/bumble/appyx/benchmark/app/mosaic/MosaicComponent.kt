package com.bumble.appyx.benchmark.app.mosaic

import androidx.compose.animation.core.SpringSpec
import com.bumble.appyx.benchmark.app.mosaic.MosaicModel.State
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.ui.DefaultAnimationSpec
import com.bumble.appyx.navigation.state.SavedStateMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MosaicComponent(
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
    savedStateMap: SavedStateMap? = null,
    gridRows: Int,
    gridCols: Int,
    pieces: List<MosaicPiece>,
    defaultAnimationSpec: SpringSpec<Float> = DefaultAnimationSpec,
    model: MosaicModel = MosaicModel(savedStateMap, gridRows, gridCols, pieces)
) : BaseAppyxComponent<MosaicPiece, State>(
    scope = scope,
    model = model,
    visualisation = { MosaicVisualisation(it, defaultAnimationSpec) },
    defaultAnimationSpec = defaultAnimationSpec
) {
    val isModelIdle = model.isIdle
}

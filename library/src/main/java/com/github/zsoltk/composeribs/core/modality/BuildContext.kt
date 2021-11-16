package com.github.zsoltk.composeribs.core.modality

import com.github.zsoltk.composeribs.core.state.SavedStateMap

data class BuildContext(
    val ancestryInfo: AncestryInfo,
    val savedStateMap: SavedStateMap?,
) {
    companion object {
        fun root(savedStateMap: SavedStateMap?): BuildContext =
            BuildContext(
                ancestryInfo = AncestryInfo.Root,
                savedStateMap = savedStateMap,
            )
    }
}

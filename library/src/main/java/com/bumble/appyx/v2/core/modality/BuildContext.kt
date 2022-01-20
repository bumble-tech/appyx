package com.bumble.appyx.v2.core.modality

import com.bumble.appyx.v2.core.state.SavedStateMap

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

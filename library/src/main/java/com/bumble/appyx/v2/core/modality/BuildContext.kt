package com.bumble.appyx.v2.core.modality

import com.bumble.appyx.utils.customisations.NodeCustomisation
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl
import com.bumble.appyx.v2.core.state.SavedStateMap

data class BuildContext(
    val ancestryInfo: AncestryInfo,
    val savedStateMap: SavedStateMap?,
    val customisations: NodeCustomisationDirectory,
) {
    companion object {
        fun root(
            savedStateMap: SavedStateMap?,
            customisations: NodeCustomisationDirectory = NodeCustomisationDirectoryImpl()
        ): BuildContext =
            BuildContext(
                ancestryInfo = AncestryInfo.Root,
                savedStateMap = savedStateMap,
                customisations = customisations
            )
    }

    fun <T : NodeCustomisation> getOrDefault(defaultCustomisation: T) : T =
        customisations.getRecursivelyOrDefault(defaultCustomisation)
}

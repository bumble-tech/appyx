package com.bumble.appyx.navigation.modality

import com.bumble.appyx.interactions.UUID
import com.bumble.appyx.interactions.core.state.MutableSavedStateMap
import com.bumble.appyx.navigation.state.SavedStateMap
import com.bumble.appyx.utils.customisations.NodeCustomisation
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectory
import com.bumble.appyx.utils.customisations.NodeCustomisationDirectoryImpl

data class BuildContext(
    val ancestryInfo: AncestryInfo,
    val savedStateMap: SavedStateMap?,
    val customisations: NodeCustomisationDirectory,
) {
    companion object {
        private const val IDENTIFIER_KEY = "build.context.identifier"

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

    val identifier: String by lazy {
        if (savedStateMap == null) {
            UUID.randomUUID()
        } else {
            savedStateMap[IDENTIFIER_KEY] as String? ?: error("onSaveInstanceState() was not called")
        }
    }

    fun <T : NodeCustomisation> getOrDefault(defaultCustomisation: T): T =
        customisations.getRecursivelyOrDefault(defaultCustomisation)

    fun onSaveInstanceState(state: MutableSavedStateMap) {
        state[IDENTIFIER_KEY] = identifier
    }
}

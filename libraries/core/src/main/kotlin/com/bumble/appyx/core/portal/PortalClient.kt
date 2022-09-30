package com.bumble.appyx.core.portal

import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.children.ChildEntryMap
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.state.SavedStateMap
import kotlinx.coroutines.flow.StateFlow

interface PortalClient<NavTarget : Any> {

    val navModel: PortalClientNavModel<NavTarget>

    /** Invoke in onBuilt() to let Portal take control of children of the current node. */
    fun attach(children: StateFlow<ChildEntryMap<NavTarget>>, lifecycle: Lifecycle)

}

internal class PortalClientImpl<NavTarget : Any>(
    savedStateMap: SavedStateMap?,
) : PortalClient<NavTarget> {
    var onAttachListener: (StateFlow<ChildEntryMap<NavTarget>>) -> Unit = {}
    var onDetachListener: () -> Unit = {}

    override val navModel: PortalClientNavModel<NavTarget> =
        PortalClientNavModel(
            savedStateMap = savedStateMap,
        )

    override fun attach(children: StateFlow<ChildEntryMap<NavTarget>>, lifecycle: Lifecycle) {
        lifecycle.subscribe(
            onCreate = { onAttachListener(children) },
            onDestroy = { onDetachListener() }
        )

    }

}

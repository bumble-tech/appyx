package com.bumble.appyx.core.portal

import com.bumble.appyx.core.state.SavedStateMap

interface PortalClientFactory {

    fun <NavTarget : Any> createPortalClient(
        savedStateMap: SavedStateMap?,
    ): PortalClient<NavTarget>

}

internal class PortalClientFactoryImpl(
    private val onClientCreated: (PortalClientImpl<*>) -> Unit,
) : PortalClientFactory {

    override fun <NavTarget : Any> createPortalClient(
        savedStateMap: SavedStateMap?,
    ): PortalClient<NavTarget> =
        PortalClientImpl<NavTarget>(savedStateMap).also(onClientCreated)

}

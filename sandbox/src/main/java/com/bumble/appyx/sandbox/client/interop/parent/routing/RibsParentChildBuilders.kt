package com.bumble.appyx.sandbox.client.interop.parent.routing

import com.badoo.ribs.builder.Builder
import com.badoo.ribs.core.Rib
import com.bumble.appyx.core.builder.SimpleBuilder
import com.bumble.appyx.sandbox.client.container.ContainerBuilder
import com.bumble.appyx.sandbox.client.interop.child.RibsChildBuilder

internal interface RibsParentChildBuilders {
    val interopNode: SimpleBuilder
    val ribsNode: Builder<Nothing?, Rib>
}

internal class RibsParentChildBuildersImpl : RibsParentChildBuilders {

    override val interopNode: SimpleBuilder = ContainerBuilder()
    override val ribsNode: Builder<Nothing?, Rib> = RibsChildBuilder()
}

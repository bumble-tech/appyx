package com.bumble.appyx.sandbox.client.interop.parent.routing

import com.badoo.ribs.builder.Builder
import com.bumble.appyx.core.builder.SimpleBuilder
import com.bumble.appyx.sandbox.client.container.ContainerBuilder
import com.bumble.appyx.sandbox.client.interop.child.RibsChild
import com.bumble.appyx.sandbox.client.interop.child.RibsChildBuilder

interface RibsParentChildBuilders {
    val interopNode: SimpleBuilder
    val ribsNode: Builder<Nothing?, RibsChild>
}

internal class RibsParentChildBuildersImpl : RibsParentChildBuilders {

    override val interopNode: SimpleBuilder = ContainerBuilder()
    override val ribsNode: Builder<Nothing?, RibsChild> = RibsChildBuilder()
}

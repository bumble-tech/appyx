package com.bumble.appyx.sample.hilt.library.onlyinternaldeps

import com.bumble.appyx.core.integration.NodeFactory
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.dagger.hilt.HiltNodeFactory
import com.bumble.appyx.sample.hilt.library.InternalDependency
import javax.inject.Inject

@HiltNodeFactory(OnlyInternalDepsLibraryNode::class)
class OnlyInternalDepsNodeFactory @Inject internal constructor(
    private val externalDepsDependency: InternalDependency
) : NodeFactory<OnlyInternalDepsLibraryNode> {
    override fun create(buildContext: BuildContext): OnlyInternalDepsLibraryNode =
        OnlyInternalDepsLibraryNode(
            buildContext = buildContext,
            dependency = externalDepsDependency
        )
}

package com.bumble.appyx.sample.hilt.library.externaldeps

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.dagger.hilt.HiltCustomNodeFactory
import com.bumble.appyx.sample.hilt.library.InternalDependency
import javax.inject.Inject

/**
 * This @HiltCustomNodeFactory annotation causes a dagger module
 * (with ActivityComponent scope) to be created.
 *
 * This module uses a Provider to ensure that the factory is only instantiated when required.
 */
@HiltCustomNodeFactory
class ExternalDepsLibraryCustomNodeFactory @Inject internal constructor(
    private val internalDependency: InternalDependency
) {
    fun create(buildContext: BuildContext) =
        ExternalDepsLibraryNode(
            buildContext = buildContext,
            dependency = internalDependency
        )
}

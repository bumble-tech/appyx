package com.bumble.appyx.sample.hilt.library.onlyinternaldeps

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.sample.hilt.library.InternalDependency

class OnlyInternalDepsLibraryNode internal constructor(
    buildContext: BuildContext,
    private val dependency: InternalDependency
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Column(modifier = modifier) {
            Text(
                "Internal dependencies only node. Activity: ${dependency.getActivityName()}"
            )
        }
    }
}

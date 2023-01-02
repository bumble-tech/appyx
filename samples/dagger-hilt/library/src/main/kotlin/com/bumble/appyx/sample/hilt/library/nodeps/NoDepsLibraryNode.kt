package com.bumble.appyx.sample.hilt.library.nodeps

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.dagger.hilt.HiltNode

@HiltNode
class NoDepsLibraryNode internal constructor(
    buildContext: BuildContext
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Column(modifier = modifier) {
            Text("No dependencies node")
        }
    }
}

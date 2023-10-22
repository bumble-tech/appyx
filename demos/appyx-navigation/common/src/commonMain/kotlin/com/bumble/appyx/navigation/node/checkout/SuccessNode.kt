package com.bumble.appyx.navigation.node.checkout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.ui.PlaceholderScreen

class SuccessNode(
    buildContext: BuildContext,
    private val onNext: () -> Unit,
) : Node(
    buildContext = buildContext
) {
    @Composable
    override fun View(modifier: Modifier) {
        PlaceholderScreen(
            modifier = modifier,
            title = "Success",
            buttonText = "Done",
            onClick = onNext
        )
    }
}

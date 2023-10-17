package com.bumble.appyx.navigation.node.viewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.utils.viewmodel.node.ViewModelNode

class ViewModelNodeExample(buildContext: BuildContext) : ViewModelNode(buildContext) {

    private val viewModels = viewModelStore

    @Composable
    @Override
    override fun View(modifier: Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {}
    }
}

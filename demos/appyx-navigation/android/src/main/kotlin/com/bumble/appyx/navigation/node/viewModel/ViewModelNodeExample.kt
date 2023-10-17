package com.bumble.appyx.navigation.node.viewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.utils.viewmodel.node.ViewModelNode

class ViewModelNodeExample(buildContext: BuildContext) : ViewModelNode(buildContext) {

    @Composable
    @Override
    override fun View(modifier: Modifier) {
        val uiState by viewModel.uiState.collectAsState(initial = UiState(0))

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Counter: ${uiState.counter}",
                fontSize = 45.sp
            )

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { viewModel.incrementCounter() }
            ) {
                Text("Increment")
            }

        }
    }
}

package com.github.zsoltk.composeribs.client.container

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.container.Container.Routing
import com.github.zsoltk.composeribs.core.RibView

class ContainerModalView(
    private val onShowModal: () -> Unit,
    private val onMakeFullScreen: () -> Unit,
) : RibView<Routing>() {

    @Composable
    override fun Compose() {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            ) {
                var fullScreenEnabled by remember { mutableStateOf(false) }

                Button(
                    enabled = !fullScreenEnabled,
                    onClick = { fullScreenEnabled = true; onShowModal() }) {
                    Text("Show modal")
                }

                Button(
                    enabled = fullScreenEnabled,
                    onClick = onMakeFullScreen
                ) {
                    Text("Make it fullscreen")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                placeholder<Routing>()
            }
        }
    }
}


package com.github.zsoltk.composeribs.client.container

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.container.Container.Routing
import com.github.zsoltk.composeribs.client.container.Container.Routing.Child1
import com.github.zsoltk.composeribs.client.container.Container.Routing.Child2
import com.github.zsoltk.composeribs.core.RibView

class ContainerView(
    private val onPushRoutingClicked: () -> Unit,
    private val onPopRoutingClicked: () -> Unit
) : RibView<Routing>() {

    @Composable
    override fun Compose() {
        Column(Modifier.padding(24.dp)) {
            Text("Container")
            Column(Modifier.padding(24.dp)) {
                // placeholder<Child1>()
                // placeholder<Child2>()
                placeholder<Routing>()
            }
            Button(onClick = onPushRoutingClicked) {
                Text(text = "Push routing")
            }
            Button(onClick = onPopRoutingClicked) {
                Text(text = "Pop routing")
            }
        }
    }
}

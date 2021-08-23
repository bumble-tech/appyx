package com.github.zsoltk.composeribs.client.container

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
        ) {
            Text("Container")

//            Column(Modifier.padding(24.dp)) {
            Box(Modifier
                .padding(top = 12.dp, bottom = 12.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
            ) {
                // placeholder<Child1>()
                // placeholder<Child2>()
                placeholder<Routing>()
            }

            Row {
                Button(onClick = onPushRoutingClicked) {
                    Text(text = "Push routing")
                }
                Spacer(modifier = Modifier.size(12.dp))
                Button(onClick = onPopRoutingClicked) {
                    Text(text = "Pop routing")
                }
            }
        }
    }
}

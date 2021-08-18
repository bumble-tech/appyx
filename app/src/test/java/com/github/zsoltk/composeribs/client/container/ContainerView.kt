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
import com.github.zsoltk.composeribs.core.Node
import com.github.zsoltk.composeribs.core.RibView

class ContainerView(
    private val onPushRoutingClicked: () -> Unit,
    private val onPopRoutingClicked: () -> Unit
) : RibView<Routing>() {

    @Composable
    override fun Compose(children: List<Routing>) {
        Column(Modifier.padding(24.dp)) {
//            PermanentChild1() // this should be good like this, by routing

//            if (children.contains())

            Text("Container")
            Column(Modifier.padding(24.dp)) {
                // v0:
//                Child1()
//                Child2()

                // v1: this should be good
//                backStack.active()

                // v2: this assumes that some other elements go to other places?? doesn't feel like a real use-case
//                if (backStack.active in listOf(Child1, Child2)) {
//                    backStack.active()
//                }

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

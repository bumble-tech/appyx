package com.github.zsoltk.composeribs.client.child2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.childn.Child.Routing
import com.github.zsoltk.composeribs.client.childn.ChildView
import com.github.zsoltk.composeribs.core.RibView

class Child2View : RibView<Routing>() {

    @Composable
    override fun Compose() {
        Column(Modifier.padding(24.dp)) {
            Text("Child2")
//            var counter by remember { mutableStateOf(1) }
//            Button(onClick = { counter++ }) {
//                Text(text = "Local state: $counter")
//            }
        }
    }
}

@Preview
@Composable
fun Child2Preview() {
    Child2View().Compose()
}

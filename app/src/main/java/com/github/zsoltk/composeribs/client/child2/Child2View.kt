package com.github.zsoltk.composeribs.client.child2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.child2.Child2.Routing
import com.github.zsoltk.composeribs.core.RibView

class Child2View : RibView<Routing>() {

    @Composable
    override fun Compose(children: List<Routing>) {
        Column(Modifier.padding(24.dp)) {
            Text("Child2")
            var counter by remember { mutableStateOf(1) }
            Button(onClick = { counter++ }) {
                Text(text = "Local state: $counter")
            }
        }
    }
}

@Preview
@Composable
fun Child2Preview() {
    Child2View().Compose(emptyList())
}

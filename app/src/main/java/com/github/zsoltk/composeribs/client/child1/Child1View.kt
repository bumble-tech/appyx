package com.github.zsoltk.composeribs.client.child1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.child1.Child1.Routing
import com.github.zsoltk.composeribs.core.RibView

class Child1View : RibView<Routing>() {

    @Composable
    override fun Compose() {
        Column(Modifier.padding(24.dp)) {
            Text("Child1")
        }
    }
}

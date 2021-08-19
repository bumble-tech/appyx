package com.github.zsoltk.composeribs.client.child1

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.github.zsoltk.composeribs.client.child1.Child1.Routing
import com.github.zsoltk.composeribs.core.RibView

class Child1View : RibView<Routing>() {

    @Composable
    override fun Compose() {
        Text("Child1")
    }
}

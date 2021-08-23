package com.github.zsoltk.composeribs.client.childn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.childn.Child.Routing
import com.github.zsoltk.composeribs.core.RibView

class ChildView(
    private val i: Int
) : RibView<Routing>() {

    @Composable
    override fun Compose() {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)

        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text("Child ($i)")
            }
        }
    }
}

@Preview
@Composable
fun ChildPreview() {
    ChildView(1).Compose()
}

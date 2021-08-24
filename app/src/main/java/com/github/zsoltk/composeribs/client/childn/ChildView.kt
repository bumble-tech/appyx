package com.github.zsoltk.composeribs.client.childn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.client.childn.Child.Routing
import com.github.zsoltk.composeribs.client.container.Container
import com.github.zsoltk.composeribs.core.RibView
import com.github.zsoltk.composeribs.ui.atomic_tangerine
import com.github.zsoltk.composeribs.ui.manatee
import com.github.zsoltk.composeribs.ui.silver_sand
import com.github.zsoltk.composeribs.ui.sizzling_red

class ChildView(
    private val i: Int
) : RibView<Routing>() {

    @Composable
    override fun Compose() {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                color = when (i) {
                    1 -> manatee
                    2 -> sizzling_red
                    3 -> atomic_tangerine
                    4 -> silver_sand
                    else -> Color.Unspecified
                },
                shape = RoundedCornerShape(6.dp)
            )
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

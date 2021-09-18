package com.github.zsoltk.composeribs.client.child

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
import com.github.zsoltk.composeribs.core.LeafNode
import com.github.zsoltk.composeribs.ui.*

class ChildNode(
    private val i: Int
) : LeafNode() {

    @Composable
    override fun View() {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                color = when (i % 4) {
                    0 -> manatee
                    1 -> sizzling_red
                    2 -> atomic_tangerine
                    3 -> silver_sand
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
    ChildNode(1).Compose()
}

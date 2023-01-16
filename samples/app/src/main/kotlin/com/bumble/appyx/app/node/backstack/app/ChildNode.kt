package com.bumble.appyx.app.node.backstack.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.R
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node

class ChildNode(
    buildContext: BuildContext,
    private val index: Int,
) : Node(buildContext = buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
        ) {
            Image(
                modifier = modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
                painter = painterResource(id = images[index % images.size]),
                contentDescription = "image",
                contentScale = ContentScale.Crop,
            )

            Text(
                modifier = Modifier.padding(12.dp),
                text = index.toString(),
                color = Color.White,
                fontSize = 36.sp
            )
        }
    }

    companion object {
        private val images = listOf(
            R.drawable.halloween3,
            R.drawable.halloween4,
            R.drawable.halloween6,
            R.drawable.halloween7,
            R.drawable.halloween8,
            R.drawable.halloween9,
            R.drawable.halloween10,
            R.drawable.halloween11,
            R.drawable.halloween12,
            R.drawable.halloween13,
            R.drawable.halloween14,
            R.drawable.halloween15,
        )
    }
}

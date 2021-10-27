package com.github.zsoltk.composeribs.client.child

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.zsoltk.composeribs.core.LeafNode
import com.github.zsoltk.composeribs.core.SavedStateMap
import com.github.zsoltk.composeribs.core.modality.BuildContext
import com.github.zsoltk.composeribs.core.routing.LocalUpNavigationHandler
import com.github.zsoltk.composeribs.ui.atomic_tangerine
import com.github.zsoltk.composeribs.ui.manatee
import com.github.zsoltk.composeribs.ui.md_amber_500
import com.github.zsoltk.composeribs.ui.md_blue_500
import com.github.zsoltk.composeribs.ui.md_blue_grey_500
import com.github.zsoltk.composeribs.ui.md_cyan_500
import com.github.zsoltk.composeribs.ui.md_grey_500
import com.github.zsoltk.composeribs.ui.md_indigo_500
import com.github.zsoltk.composeribs.ui.md_light_blue_500
import com.github.zsoltk.composeribs.ui.md_light_green_500
import com.github.zsoltk.composeribs.ui.md_lime_500
import com.github.zsoltk.composeribs.ui.md_pink_500
import com.github.zsoltk.composeribs.ui.md_teal_500
import com.github.zsoltk.composeribs.ui.silver_sand
import com.github.zsoltk.composeribs.ui.sizzling_red
import kotlin.random.Random

class ChildNode(
    private val i: Int,
    buildContext: BuildContext
) : LeafNode(
    buildContext = buildContext,
) {

    private val colors = listOf(
        manatee,
        sizzling_red,
        atomic_tangerine,
        silver_sand,
        md_pink_500,
        md_indigo_500,
        md_blue_500,
        md_light_blue_500,
        md_cyan_500,
        md_teal_500,
        md_light_green_500,
        md_lime_500,
        md_amber_500,
        md_grey_500,
        md_blue_grey_500
    )

    private val colorIndex =
        buildContext.savedStateMap?.get(KEY_COLOR_INDEX) as? Int ?: Random.nextInt(colors.size)
    private val color = colors[colorIndex]

    override fun onSaveInstanceState(scope: SaverScope): SavedStateMap =
        super.onSaveInstanceState(scope) + mapOf(KEY_COLOR_INDEX to colorIndex)

    @Composable
    override fun View() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = color,
                    shape = RoundedCornerShape(6.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                val upNavigationHandler = LocalUpNavigationHandler.current
                Text("Child ($i)")
                Row {
                    // Local UI state should be saved too (both in backstack and onSaveInstanceState)
                    var counter by rememberSaveable { mutableStateOf(0) }
                    Text(text = "Counter $counter", modifier = Modifier.align(CenterVertically))
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { counter++ }) {
                        Text("Increment")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { upNavigation(upNavigationHandler) }) {
                        Text("Go up")
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_COLOR_INDEX = "ColorIndex"
    }
}

@Preview
@Composable
fun ChildPreview() {
    ChildNode(1, BuildContext.root(null)).Compose()
}

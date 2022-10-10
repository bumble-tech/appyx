package com.bumble.appyx.sandbox.client.child

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.state.MutableSavedStateMap
import com.bumble.appyx.sandbox.ui.atomic_tangerine
import com.bumble.appyx.sandbox.ui.manatee
import com.bumble.appyx.sandbox.ui.md_amber_500
import com.bumble.appyx.sandbox.ui.md_blue_500
import com.bumble.appyx.sandbox.ui.md_blue_grey_500
import com.bumble.appyx.sandbox.ui.md_cyan_500
import com.bumble.appyx.sandbox.ui.md_grey_500
import com.bumble.appyx.sandbox.ui.md_indigo_500
import com.bumble.appyx.sandbox.ui.md_light_blue_500
import com.bumble.appyx.sandbox.ui.md_light_green_500
import com.bumble.appyx.sandbox.ui.md_lime_500
import com.bumble.appyx.sandbox.ui.md_pink_500
import com.bumble.appyx.sandbox.ui.md_teal_500
import com.bumble.appyx.sandbox.ui.silver_sand
import com.bumble.appyx.sandbox.ui.sizzling_red
import kotlin.random.Random

class ChildNode(
    private val name: String,
    buildContext: BuildContext,
    isPortal: Boolean = true
) : Node(
    buildContext = buildContext,
    isPortal = isPortal
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

    override fun onSaveInstanceState(state: MutableSavedStateMap) {
        super.onSaveInstanceState(state)
        state[KEY_COLOR_INDEX] = colorIndex
    }

    @Composable
    override fun View(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = color,
                    shape = RoundedCornerShape(6.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text("Child ($name).")
                Row {
                    // Local UI state should be saved too (both in backstack and onSaveInstanceState)
                    var counter by rememberSaveable { mutableStateOf(0) }
                    Text(text = "Counter $counter", modifier = Modifier.align(CenterVertically))
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { counter++ }) {
                        Text("Increment")
                    }
                }
                Row {
                    Button(onClick = { navigateUp() }) {
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
    ChildNode("1", BuildContext.root(null)).Compose()
}

package com.bumble.appyx.appyxnavigation.node.child

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.coroutineScope
import com.bumble.appyx.appyxnavigation.ui.atomic_tangerine
import com.bumble.appyx.appyxnavigation.ui.manatee
import com.bumble.appyx.appyxnavigation.ui.md_amber_500
import com.bumble.appyx.appyxnavigation.ui.md_blue_500
import com.bumble.appyx.appyxnavigation.ui.md_blue_grey_500
import com.bumble.appyx.appyxnavigation.ui.md_cyan_500
import com.bumble.appyx.appyxnavigation.ui.md_grey_500
import com.bumble.appyx.appyxnavigation.ui.md_indigo_500
import com.bumble.appyx.appyxnavigation.ui.md_light_blue_500
import com.bumble.appyx.appyxnavigation.ui.md_light_green_500
import com.bumble.appyx.appyxnavigation.ui.md_lime_500
import com.bumble.appyx.appyxnavigation.ui.md_pink_500
import com.bumble.appyx.appyxnavigation.ui.md_teal_500
import com.bumble.appyx.appyxnavigation.ui.silver_sand
import com.bumble.appyx.appyxnavigation.ui.sizzling_red
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.IntegrationPointStub
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.modality.BuildContext.Companion.root
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.state.MutableSavedStateMap
import kotlinx.coroutines.delay
import kotlin.random.Random

class GenericChildNode(
    buildContext: BuildContext,
    counterStartValue: Int
) : Node(
    buildContext = buildContext
) {

    companion object {
        private const val KEY_COUNTER = "Counter"
        private const val KEY_COLOR_INDEX = "ColorIndex"
    }

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

    private var counter by mutableStateOf(
        buildContext.savedStateMap?.get(KEY_COUNTER) as? Int ?: counterStartValue
    )
    private var colorIndex by mutableStateOf(
        buildContext.savedStateMap?.get(KEY_COLOR_INDEX) as? Int ?: Random.nextInt(colors.size)
    )

    init {
        lifecycle.coroutineScope.launchWhenCreated {
            while (true) {
                counter++
                delay(1000)
            }
        }
    }

    override fun onSaveInstanceState(state: MutableSavedStateMap) {
        super.onSaveInstanceState(state)
        state[KEY_COUNTER] = counter
        state[KEY_COLOR_INDEX] = colorIndex
    }

    @Composable
    override fun View(modifier: Modifier) {
        val color by remember { derivedStateOf { colors[colorIndex] } }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = color,
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable {
                    colorIndex = Random.nextInt(colors.size)
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                Text(
                    text = "Child (${id.substring(0, 4)})",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$counter",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 40.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun GenericChildNodePreview() {
    Box(Modifier.size(200.dp)) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            GenericChildNode(root(null), 100)
        }
    }
}

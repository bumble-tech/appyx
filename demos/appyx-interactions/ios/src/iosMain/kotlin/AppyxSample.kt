
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.interactions.composable.AppyxInteractionsContainer
import com.bumble.appyx.interactions.model.Element
import com.bumble.appyx.interactions.model.BaseAppyxComponent
import com.bumble.appyx.interactions.ui.helper.AppyxComponentSetup
import theme.color_primary
import theme.md_amber_500
import theme.md_blue_500
import theme.md_blue_grey_500
import theme.md_cyan_500
import theme.md_grey_500
import theme.md_indigo_500
import theme.md_light_blue_500
import theme.md_light_green_500
import theme.md_lime_500
import theme.md_pink_500
import theme.md_teal_500
import kotlin.random.Random
import SampleInteractionTarget.Element as SampleInteractionTargetElement

@Suppress("UnstableCollections") // actions parameter
@Composable
internal fun <InteractionTarget : Any, ModelState : Any> AppyxSample(
    screenWidthPx: Int,
    screenHeightPx: Int,
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    actions: Map<String, () -> Unit>,
    modifier: Modifier = Modifier,
    elementUi: @Composable (Element<InteractionTarget>) -> Unit = {
        ElementUi(element = it)
    }
) {
    AppyxComponentSetup(appyxComponent)

    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally,
    ) {
        AppyxInteractionsContainer(
            appyxComponent = appyxComponent,
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            modifier = Modifier.weight(0.9f).clipToBounds()
        ) { 
            elementUi(it)
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(top = 8.dp),
        ) {
            actions.keys.forEachIndexed { _, key ->
                Action(text = key, action = actions.getValue(key))
            }
        }
    }
}

@Composable
internal fun <NavTarget : Any> ElementUi(
    element: Element<NavTarget>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = when (val target = element.interactionTarget) {
                    is SampleInteractionTargetElement -> colors.getOrElse(target.idx % colors.size) { Color.Cyan }
                    else -> {
                        Color.Cyan
                    }
                }
            )
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = element.interactionTarget.toString(),
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun Action(
    text: String,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color_primary, shape = RoundedCornerShape(4.dp))
            .clickable { action.invoke() }
            .padding(horizontal = 18.dp, vertical = 4.dp)
    ) {
        Text(text)
    }
}

internal sealed class SampleInteractionTarget {
    data class Element(val idx: Int = Random.nextInt(1, 100)) : SampleInteractionTarget() {
        override fun toString(): String =
            "Element $idx"
    }
}

val colors = listOf(
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
    md_blue_grey_500,
)

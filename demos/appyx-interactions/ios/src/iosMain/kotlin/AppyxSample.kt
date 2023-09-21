import InteractionTarget.Element
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
import com.bumble.appyx.interactions.core.AppyxComponent
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.ui.helper.AppyxComponentSetup
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
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

internal sealed class InteractionTarget {
    data class Element(val idx: Int = Random.nextInt(1, 100)) : InteractionTarget() {
        override fun toString(): String =
            "Element $idx"
    }
}

@Composable
internal fun AppyxSample(
    screenWidthPx: Int,
    screenHeightPx: Int,
    appyxComponent: BaseAppyxComponent<InteractionTarget, Any>,
    actions: Map<String, () -> Unit>,
    modifier: Modifier = Modifier,
) {
    AppyxComponentSetup(appyxComponent)

    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally,
    ) {
        AppyxComponent(
            appyxComponent = appyxComponent,
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            modifier = Modifier.weight(0.9f).clipToBounds()
        ) { elementUiModel ->
            ModalUi(elementUiModel = elementUiModel)
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
internal fun ModalUi(
    elementUiModel: ElementUiModel<InteractionTarget>,
    modifier: Modifier = Modifier
) {
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(elementUiModel.modifier)
            .background(
                color = when (val target = elementUiModel.element.interactionTarget) {
                    is Element -> colors.getOrElse(target.idx % colors.size) { Color.Cyan }
                }
            )
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = elementUiModel.element.interactionTarget.toString(),
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

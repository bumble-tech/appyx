package com.bumble.appyx.appyxnavigation.node.spotlight.debug

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.appyxnavigation.colors
import com.bumble.appyx.appyxnavigation.composable.KnobControl
import com.bumble.appyx.appyxnavigation.node.spotlight.debug.SpotlightDebugNode.NavTarget
import com.bumble.appyx.appyxnavigation.ui.appyx_dark
import com.bumble.appyx.navigation.composable.Children
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.interpolator.SpotlightSlider
import kotlinx.coroutines.CoroutineScope
import kotlinx.parcelize.Parcelize

class SpotlightDebugNode(
    buildContext: BuildContext,
    coroutineScope: CoroutineScope,
    private val spotlight: Spotlight<NavTarget> = Spotlight(
        model = SpotlightModel(
            items = List(7) { NavTarget.Child(it) },
            initialActiveIndex = 0f,
            initialActiveWindow = 1f
        ),
        interpolator = { SpotlightSlider(it, coroutineScope) },
        isDebug = true
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    interactionModel = spotlight
) {

    init {
//        spotlight.next(mode = Operation.Mode.KEYFRAME)
//        spotlight.next(mode = Operation.Mode.KEYFRAME)
//        spotlight.next(mode = Operation.Mode.KEYFRAME)
//        spotlight.previous(mode = Operation.Mode.KEYFRAME)
//        spotlight.last(mode = Operation.Mode.KEYFRAME)
//        spotlight.first(mode = Operation.Mode.KEYFRAME)
    }

    sealed class NavTarget : Parcelable {
        @Parcelize
        class Child(val index: Int) : NavTarget()
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.Child -> node(buildContext) {
                val backgroundColor = remember { colors.shuffled().random() }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(5))
                        .background(backgroundColor)
                        .padding(24.dp)
                ) {
                    Text(
                        text = navTarget.index.toString(),
                        fontSize = 21.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

    @ExperimentalMaterialApi
    @Composable
    override fun View(modifier: Modifier) {
        Column(
            modifier
                .fillMaxWidth()
                .background(appyx_dark)
        ) {
            KnobControl(onValueChange = {
                spotlight.setNormalisedProgress(it)
            })
            Children(
                interactionModel = spotlight,
                modifier = Modifier.padding(
                    horizontal = 64.dp,
                    vertical = 12.dp
                )
            )
        }
    }
}

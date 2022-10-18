package com.bumble.appyx.sandbox2.navmodel2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.bumble.appyx.core.navigation.transition.TransitionBounds
import com.bumble.appyx.core.navigation.transition.TransitionParams
import com.bumble.appyx.sandbox2.ui.theme.atomic_tangerine
import com.bumble.appyx.sandbox2.ui.theme.manatee
import com.bumble.appyx.sandbox2.ui.theme.md_amber_500
import com.bumble.appyx.sandbox2.ui.theme.md_blue_500
import com.bumble.appyx.sandbox2.ui.theme.md_blue_grey_500
import com.bumble.appyx.sandbox2.ui.theme.md_cyan_500
import com.bumble.appyx.sandbox2.ui.theme.md_grey_500
import com.bumble.appyx.sandbox2.ui.theme.md_indigo_500
import com.bumble.appyx.sandbox2.ui.theme.md_light_blue_500
import com.bumble.appyx.sandbox2.ui.theme.md_light_green_500
import com.bumble.appyx.sandbox2.ui.theme.md_lime_500
import com.bumble.appyx.sandbox2.ui.theme.md_pink_500
import com.bumble.appyx.sandbox2.ui.theme.md_teal_500
import com.bumble.appyx.sandbox2.ui.theme.silver_sand
import com.bumble.appyx.sandbox2.ui.theme.sizzling_red

enum class NavTarget {
    Child1, Child2, Child3, Child4, Child5, Child6, Child7, Child8, Child9, Child10
}

val colors = listOf(
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

@Composable
fun createTransitionParams(
    elementSize: IntSize,
): State<TransitionParams> {
    val density = LocalDensity.current.density

    return remember(elementSize) {
        derivedStateOf {
            TransitionParams(
                bounds = TransitionBounds(
                    width = Dp(elementSize.width / density),
                    height = Dp(elementSize.height / density)
                )
            )
        }
    }
}

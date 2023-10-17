package com.bumble.appyx.interactions.bottomnav

import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.bumble.appyx.components.experimental.cards.android.DatingCards
import com.bumble.appyx.components.experimental.puzzle15.android.Puzzle15
import com.bumble.appyx.components.internal.testdrive.android.TestDriveExperiment
import com.bumble.appyx.components.spotlight.ui.sliderrotation.SpotlightSliderRotation
import com.bumble.appyx.interactions.sample.SpotlightExperiment
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.utils.material3.AppyxNavItem
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

@Suppress("ForbiddenComment")
//TODO: Fix gradle issue with using extended material icons
@Parcelize
enum class MainNavItem : Parcelable {
    CARDS, SPOTLIGHT, TEST_DRIVE, PUZZLE;

    companion object {
        val resolver: (MainNavItem) -> AppyxNavItem = { navBarItem ->
            when (navBarItem) {
                CARDS -> AppyxNavItem(
                    text = "Cards",
                    unselectedIcon = Outlined.Star,
                    selectedIcon = Filled.Star,
                    iconModifier = Modifier.rotate(180f),
                    node = { node(it) { DatingCards() } }
                )

                SPOTLIGHT -> AppyxNavItem(
                    text = "Spotlight",
                    unselectedIcon = Outlined.Star,
                    selectedIcon = Filled.Star,
                    node = { node(it) { SpotlightExperiment { SpotlightSliderRotation(it) } } }
                )

                TEST_DRIVE -> AppyxNavItem(
                    text = "TestDrive",
                    unselectedIcon = Outlined.Star,
                    selectedIcon = Filled.GridViewCustom,
                    node = { node(it) { TestDriveExperiment() } }
                )

                PUZZLE -> AppyxNavItem(
                    text = "Puzzle",
                    unselectedIcon = Outlined.Star,
                    selectedIcon = Filled.Star,
                    node = { node(it) { Puzzle15() } }
                )

            }
        }
    }
}

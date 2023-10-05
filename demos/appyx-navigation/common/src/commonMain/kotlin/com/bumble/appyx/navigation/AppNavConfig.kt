package com.bumble.appyx.navigation

import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.WebStories
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.ViewCarousel
import androidx.compose.material.icons.outlined.WebStories
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.bumble.appyx.navigation.node.backstack.BackStackExamplesNode
import com.bumble.appyx.navigation.node.promoter.PromoterNode
import com.bumble.appyx.navigation.node.spotlight.SpotlightNode
import com.bumble.appyx.navigation.ui.icons.GridViewCustom
import com.bumble.appyx.utils.material3.AppyxNavItem
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
enum class MainNavItem : Parcelable {
    BACKSTACK, SPOTLIGHT, PROMOTER;

    companion object {
        val resolver: (MainNavItem) -> AppyxNavItem = { navBarItem ->
            when (navBarItem) {
                BACKSTACK -> AppyxNavItem(
                    text = "Back stack",
                    unselectedIcon = Outlined.WebStories,
                    selectedIcon = Filled.WebStories,
                    iconModifier = Modifier.rotate(180f),
                    node = { BackStackExamplesNode(it) }
                )

                SPOTLIGHT -> AppyxNavItem(
                    text = "Spotlight",
                    unselectedIcon = Outlined.ViewCarousel,
                    selectedIcon = Filled.ViewCarousel,
                    node = { SpotlightNode(it) }
                )

                PROMOTER -> AppyxNavItem(
                    text = "Promoter",
                    unselectedIcon = Outlined.GridView,
                    selectedIcon = Filled.GridViewCustom,
                    iconModifier = Modifier.rotate(45f),
                    node = { PromoterNode(it) }
                )
            }
        }
    }
}




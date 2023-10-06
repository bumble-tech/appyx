package com.bumble.appyx.navigation.node.main

import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import com.bumble.appyx.navigation.node.cakes.CakeCategoryNode
import com.bumble.appyx.navigation.node.home.HomeNode
import com.bumble.appyx.navigation.node.profile.ProfileNode
import com.bumble.appyx.utils.material3.AppyxNavItem
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
enum class MainNavItem : Parcelable {
    CAKES, HOME, PROFILE;

    companion object {
        val resolver: (MainNavItem) -> AppyxNavItem = { navBarItem ->
            when (navBarItem) {
                CAKES -> AppyxNavItem(
                    text = "Cakes",
                    unselectedIcon = Outlined.Cake,
                    selectedIcon = Filled.Cake,
                    node = { CakeCategoryNode(it) }
                )

                HOME -> AppyxNavItem(
                    text = "Home",
                    unselectedIcon = Outlined.Home,
                    selectedIcon = Filled.Home,
                    node = { HomeNode(it) }
                )

                PROFILE -> AppyxNavItem(
                    text = "Profile",
                    unselectedIcon = Outlined.Person,
                    selectedIcon = Filled.Person,
                    node = { ProfileNode(it) }
                )
            }
        }
    }
}

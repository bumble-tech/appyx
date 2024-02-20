package com.bumble.appyx.demos.navigation.node.main

import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.Icons.Outlined
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import com.bumble.appyx.demos.navigation.node.cakes.CakeListNode
import com.bumble.appyx.demos.navigation.node.cart.Cart
import com.bumble.appyx.demos.navigation.node.checkout.checkoutNode
import com.bumble.appyx.demos.navigation.node.home.HomeNode
import com.bumble.appyx.demos.navigation.node.profile.ProfileNode
import com.bumble.appyx.demos.navigation.node.profile.User
import com.bumble.appyx.utils.material3.AppyxNavItem
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import kotlinx.coroutines.flow.map

@Parcelize
enum class MainNavItem : Parcelable {
    CAKES, HOME, PROFILE, CART;

    companion object {
        fun resolver(
            user: User,
            cart: Cart,
            onLogout: () -> Unit
        ): (MainNavItem) -> AppyxNavItem = { navBarItem ->
            when (navBarItem) {
                CAKES -> AppyxNavItem(
                    text = "Cakes",
                    unselectedIcon = Outlined.Cake,
                    selectedIcon = Filled.Cake,
                    node = { CakeListNode(it, cart) }
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
                    node = { ProfileNode(it, user, onLogout) }
                )

                CART -> AppyxNavItem(
                    text = "Cart",
                    unselectedIcon = Outlined.ShoppingCart,
                    selectedIcon = Filled.ShoppingCart,
                    badgeText = cart.items.map {
                        val nItems = it.values.sum()
                        if (nItems != 0) {
                            nItems.toString()
                        } else {
                            null
                        }
                    },
                    node = { checkoutNode(it, cart) }
                )
            }
        }
    }
}

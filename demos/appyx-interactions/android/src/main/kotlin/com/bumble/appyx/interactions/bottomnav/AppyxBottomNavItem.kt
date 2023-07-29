package com.bumble.appyx.interactions.bottomnav

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

class AppyxBottomNavItem(
    val text: @Composable (isSelected: Boolean) -> Unit,
    val icon: @Composable (isSelected: Boolean) -> Unit,
    val content: @Composable () -> Unit
) {
    constructor(
        text: String,
        unselectedIcon: ImageVector,
        selectedIcon: ImageVector,
        content: @Composable () -> Unit,
        iconModifier: Modifier = Modifier
    ) : this(
        text = { Text(text) },
        icon = { isSelected ->
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = text,
                modifier = iconModifier
            )
        },
        content = content
    )
}

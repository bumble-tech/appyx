package com.bumble.appyx.utils.material3

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node


class AppyxNavItem(
    val text: @Composable (isSelected: Boolean) -> Unit,
    val icon: @Composable (isSelected: Boolean) -> Unit,
    val node: (buildContext: BuildContext) -> Node
) {
    constructor(
        text: String,
        unselectedIcon: ImageVector,
        selectedIcon: ImageVector,
        iconModifier: Modifier = Modifier,
        node: (buildContext: BuildContext) -> Node
    ) : this(
        text = {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        icon = { isSelected ->
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = text,
                modifier = iconModifier,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        node = node
    )
}

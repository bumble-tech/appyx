package com.bumble.appyx.utils.material3

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class AppyxNavItem(
    val text: @Composable (isSelected: Boolean) -> Unit,
    val icon: @Composable (isSelected: Boolean) -> Unit,
    val node: (buildContext: BuildContext) -> Node
) {
    @OptIn(ExperimentalMaterial3Api::class)
    constructor(
        text: String,
        unselectedIcon: ImageVector,
        selectedIcon: ImageVector,
        badgeText: Flow<String?> = MutableStateFlow(null),
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
            BadgedBox(
                badge = {
                    val badgeText = badgeText.collectAsState(null).value
                    if (badgeText != null) {
                        Badge {
                            Text(
                                text = badgeText,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.semantics {
                                    contentDescription = badgeText
                                }
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (isSelected) selectedIcon else unselectedIcon,
                    contentDescription = text,
                    modifier = iconModifier,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        node = node
    )
}

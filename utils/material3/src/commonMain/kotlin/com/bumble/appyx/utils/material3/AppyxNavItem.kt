package com.bumble.appyx.utils.material3

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


@Suppress("MagicNumber")
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
        hasScaleAnimation: Boolean = true,
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
                    val badgeCurrentText = badgeText.collectAsState(null).value
                    val scale = if (hasScaleAnimation) {
                        var animated by remember { mutableStateOf(false) }
                        DisposableEffect(badgeCurrentText) {
                            animated = true
                            onDispose { animated = false }
                        }
                        animateFloatAsState(
                            targetValue = if (animated) 1.2f else 1f,
                            finishedListener = { animated = false },
                        ).value
                    } else {
                        1f
                    }

                    if (badgeCurrentText != null) {
                        Badge(Modifier.scale(scale)) {
                            Text(
                                text = badgeCurrentText,
                                color = MaterialTheme.colorScheme.primaryContainer,
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

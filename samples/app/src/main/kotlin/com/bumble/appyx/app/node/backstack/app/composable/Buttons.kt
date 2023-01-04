package com.bumble.appyx.app.node.backstack.app.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.app.ui.appyx_dark
import com.bumble.appyx.app.ui.appyx_yellow1

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier.padding(horizontal = 8.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = appyx_yellow1,
            contentColor = appyx_dark
        )
    ) { content() }
}

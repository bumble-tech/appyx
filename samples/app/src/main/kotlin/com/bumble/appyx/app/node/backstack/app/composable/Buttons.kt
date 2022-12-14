package com.bumble.appyx.app.node.backstack.app.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
            backgroundColor = appyx_yellow1,
            contentColor = appyx_dark
        )
    ) { content() }
}

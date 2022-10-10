package com.bumble.appyx.sandbox

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(modifier = modifier, onClick = onClick) {
        Text(textAlign = TextAlign.Center, text = text)
    }
}

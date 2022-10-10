package com.bumble.appyx.sandbox

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign

@Composable
fun TextButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(textAlign = TextAlign.Center, text = text)
    }
}

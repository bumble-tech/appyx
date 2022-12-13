package com.bumble.appyx.app.node.backstack.app.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumble.appyx.R

@Composable
fun LogoHeader(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.background)
    ) {
        Logo(onClick = onClick)
    }
}

@Composable
fun Logo(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Appyx Logo",
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() }
    )
}

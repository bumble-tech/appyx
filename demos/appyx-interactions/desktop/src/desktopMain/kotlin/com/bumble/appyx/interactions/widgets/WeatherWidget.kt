package com.bumble.appyx.interactions.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherWidget(
    currentTemperature: Float,
    lowTemperature: Float,
    highTemperature: Float,
    modifier: Modifier = Modifier,
) {
    Widget(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource("sky.png"),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                text = "High: $highTemperature° Low: $lowTemperature°",
                fontSize = 16.sp,
                color = Color.White,
            )
            Text(
                text = "$currentTemperature°",
                fontSize = 24.sp,
                color = Color.White,
            )
        }
    }
}

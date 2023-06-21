package com.bumble.appyx.samples.common.widget

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalendarWidget(
    modifier: Modifier = Modifier,
) {
    Widget(
        bgTopColor = Color.White,
        bgBottomColor = Color.LightGray,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Event(time = "11:00am", event = "Stand-up Meeting")
            Separator()
            Event(time = "12:00pm", event = "Workshop: Jetpack Compose state hoisting")
            Separator()
            Event(time = " 1:00pm", event = "Break: Lunch")
            Separator()
            Event(time = " 2:00pm", event = "Workshop: Gesture handling with Jetpack Compose")
        }
    }
}

@Composable
private fun Event(
    time: String,
    event: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = time,
            textAlign = TextAlign.End,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = modifier
                .requiredWidth(80.dp)
                .padding(end = 8.dp),
        )
        Text(
            text = event,
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = modifier.weight(1f),
        )
    }
}

@Composable
private fun Separator(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 80.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            .requiredHeight(1.dp)
            .border(1.dp, Color(0.4f, 0.4f, 0.4f))
    )
}

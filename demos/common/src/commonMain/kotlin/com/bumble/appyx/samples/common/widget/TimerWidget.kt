package com.bumble.appyx.samples.common.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI

@Composable
fun TimerWidget(
    timeInitial: Long = 89764,
    modifier: Modifier = Modifier,
) {
    val timeLeft by remember { mutableStateOf(timeInitial) }
    val minutes by remember { derivedStateOf { (timeLeft / 60000).formatted() } }
    val seconds by remember { derivedStateOf { ((timeLeft % 60000) / 1000).formatted() } }
    val millis by remember { derivedStateOf { ((timeLeft % 1000) / 10).formatted() } }
    Widget(
        bgTopColor = Color(0.285f, 0.285f, 0.285f),
        bgBottomColor = Color(0.175f, 0.175f, 0.175f),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "$minutes:$seconds",
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                )
                Text(
                    text = ":$millis",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                )
            }
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val radius = size.minDimension / 2f
                val circleLength = (2f * PI * radius).toFloat()
                val intervalLength = circleLength / 180f
                drawCircle(
                    color = Color.White,
                    style = Stroke(
                        width = 8f * density,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = arrayOf(intervalLength, intervalLength).toFloatArray(),
                        )
                    ),
                )
            }
        }
    }
}

// JS doesn't have support for String.format
private fun Long.formatted(): String =
    toString().run {
        if (length < 2) {
            "0$this"
        } else {
            this
        }
    }

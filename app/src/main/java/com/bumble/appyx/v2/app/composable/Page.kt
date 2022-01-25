package com.bumble.appyx.v2.app.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
@ExperimentalUnitApi
fun Page(
    modifier: Modifier,
    title: String,
    body: String,
    illustration: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxWidth()
        ) {
            illustration()
        }

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(0.35f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Text(
                text = body,
                style = MaterialTheme.typography.body1.copy(
                    lineHeight = TextUnit(1.5f, TextUnitType.Em)
                )
            )
        }

    }
}

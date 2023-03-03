package com.bumble.appyx.navigation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
@ExperimentalUnitApi
fun Page(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    illustration: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 24.dp
            )
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
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = TextUnit(1.5f, TextUnitType.Em)
                    )
                )
            }

        }

    }
}

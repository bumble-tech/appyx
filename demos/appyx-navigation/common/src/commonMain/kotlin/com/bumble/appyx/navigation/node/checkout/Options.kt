package com.bumble.appyx.navigation.node.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.collections.ImmutableList

@Composable
fun Options(
    values: ImmutableList<CheckoutFormField>,
    selected: CheckoutFormField,
    onUpdate: (CheckoutFormField) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        values.forEach { formField ->
            RadioOption(
                isSelected = formField == selected,
                onToggled = { onUpdate(formField) },
                text = formField.value
            )
        }
    }
}

@Composable
private fun RadioOption(
    isSelected: Boolean,
    onToggled: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                onClick = onToggled,
                indication = null,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onToggled
        )
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

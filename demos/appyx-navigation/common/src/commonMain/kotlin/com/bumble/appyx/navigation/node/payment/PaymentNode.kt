package com.bumble.appyx.navigation.node.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node

class PaymentNode(
    buildContext: BuildContext,
    private val confirmAction: () -> Unit,
) : Node(
    buildContext = buildContext,
) {

    @Composable
    override fun View(modifier: Modifier) {
        PaymentDetails(modifier)
    }

    @Composable
    private fun PaymentDetails(modifier: Modifier) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                item("Header") {
                    Text(
                        text = "Payment Details",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                }
                item("Spacer" ) { Spacer(modifier = Modifier.requiredHeight(8.dp)) }

                item("Card no") {
                    PaymentText(header = "Card number:", value = "4555 2212 3323 1213")
                }
                item("Expiry") {
                    PaymentText(header = "Expiry:", value = "12/25")
                }
                item("Name") {
                    PaymentText(header = "Card holder name:", value = "One who desires cake")
                }
                item("CVV") {
                    PaymentText(header = "CVV:", value = "***")
                }
            }

            ConfirmAction(confirmAction)
        }
    }

    @Composable
    private fun PaymentText(
        header: String,
        value: String,
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(text = header, style = MaterialTheme.typography.titleMedium)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }

    @Composable
    private fun ConfirmAction(
        confirmAction: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Button(
            onClick = confirmAction,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text("Process Payment")
        }
    }
}

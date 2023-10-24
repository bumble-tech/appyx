package com.bumble.appyx.navigation.node.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.checkout.PaymentOption.Card
import com.bumble.appyx.navigation.node.checkout.PaymentOption.HugsHighFives
import com.bumble.appyx.navigation.node.checkout.PaymentOption.Monopoly
import com.bumble.appyx.navigation.node.checkout.PaymentOption.PiggyBank

sealed class PaymentOption(override val value: String) : CheckoutFormField {
    object Card : PaymentOption("Card")
    object Monopoly : PaymentOption("Monopoly money")
    object PiggyBank : PaymentOption("Piggy bank payment")
    object HugsHighFives : PaymentOption("Hugs and high fives")
}

private val paymentOptions = listOf(
    Card,
    Monopoly,
    PiggyBank,
    HugsHighFives
)

class PaymentNode(
    buildContext: BuildContext,
    private val onPaymentConfirmed: () -> Unit,
) : Node(
    buildContext = buildContext,
) {

    @Composable
    override fun View(modifier: Modifier) {
        PaymentDetails(modifier)
    }

    @Composable
    private fun PaymentDetails(modifier: Modifier) {
        var paymentOption by remember { mutableStateOf<CheckoutFormField>(Card) }

        Surface(
            modifier = modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Payment Details",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.requiredHeight(4.dp))
                    Options(
                        values = paymentOptions,
                        selected = paymentOption,
                        onUpdate = { paymentOption = it }
                    )
                    Action(
                        action = onPaymentConfirmed,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

    @Composable
    private fun Action(
        action: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Button(
            onClick = action,
            modifier = modifier,
        ) {
            Text("Process Payment")
        }
    }
}

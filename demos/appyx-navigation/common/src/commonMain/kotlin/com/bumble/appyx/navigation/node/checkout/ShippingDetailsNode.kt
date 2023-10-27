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
import com.bumble.appyx.navigation.collections.toImmutableList
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.checkout.ShippingMethod.Pony
import com.bumble.appyx.navigation.node.checkout.ShippingMethod.Rocket
import com.bumble.appyx.navigation.node.checkout.ShippingMethod.Snail
import com.bumble.appyx.navigation.node.checkout.ShippingMethod.Teleportation

sealed class ShippingMethod(override val value: String): CheckoutFormField {
    object Pony : ShippingMethod("Pony express")
    object Snail : ShippingMethod("Snail mail express")
    object Rocket : ShippingMethod("Rocket ship delivery")
    object Teleportation : ShippingMethod("Teleportation")
}

private val shippingMethods = listOf(
    Pony,
    Snail,
    Rocket,
    Teleportation
)

class ShippingDetailsNode(
    buildContext: BuildContext,
    private val onShippingConfirmed: () -> Unit,
) : Node(
    buildContext = buildContext,
) {

    @Composable
    override fun View(modifier: Modifier) {
        ShippingDetails(modifier)
    }

    @Composable
    private fun ShippingDetails(modifier: Modifier = Modifier) {
        var shippingMethod by remember { mutableStateOf<CheckoutFormField>(Pony) }

        Surface(
            modifier = modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Shipping Details",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.requiredHeight(4.dp))
                    Options(
                        values = shippingMethods.toImmutableList(),
                        selected = shippingMethod,
                        onUpdate = { shippingMethod = it }
                    )
                    Action(
                        action = onShippingConfirmed,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

    @Composable
    private fun Action(action: () -> Unit, modifier: Modifier = Modifier) {
        Button(
            onClick = action,
            modifier = modifier,
        ) {
            Text("Proceed to payment")
        }
    }
}

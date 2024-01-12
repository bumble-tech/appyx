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
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.checkout.Address.AddressOne
import com.bumble.appyx.navigation.node.checkout.Address.AddressTwo
import com.bumble.appyx.navigation.node.checkout.Address.AddressThree

sealed class Address(override val value: String): CheckoutFormField {
    object AddressOne : Address("Address one")
    object AddressTwo : Address("Address two")
    object AddressThree : Address("Address three")
}

private val addresses = listOf(
    AddressOne,
    AddressTwo,
    AddressThree
)

class AddressNode(
    nodeContext: NodeContext,
    private val onAddressSelected: () -> Unit,
) : Node(
    nodeContext = nodeContext
) {
    @Composable
    override fun Content(modifier: Modifier) {
        AddressSelection(modifier)
    }

    @Composable
    private fun AddressSelection(modifier: Modifier = Modifier) {
        var selectedAddress by remember { mutableStateOf<CheckoutFormField>(AddressOne) }

        Surface(
            modifier = modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Select Address",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.requiredHeight(4.dp))
                    Options(
                        values = addresses.toImmutableList(),
                        selected = selectedAddress,
                        onUpdate = { selectedAddress = it }
                    )
                    Action(
                        action = onAddressSelected,
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
            Text("Proceed to shipping")
        }
    }
}

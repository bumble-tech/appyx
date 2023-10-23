package com.bumble.appyx.navigation.node.shippingdetails

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

class ShippingDetailsNode(
    buildContext: BuildContext,
    private val confirmAction: () -> Unit,
) : Node(
    buildContext = buildContext,
) {

    @Composable
    override fun View(modifier: Modifier) {
        ShippingDetails(modifier)
    }

    @Composable
    private fun ShippingDetails(modifier: Modifier) {
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
                        text = "Shipping Details",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                }
                item("Spacer" ) { Spacer(modifier = Modifier.requiredHeight(8.dp)) }

                item("Name") {
                    ShippingText(header = "Name:", value = "Cake wisher")
                }
                item("Address") {
                    ShippingText(header = "Address:", value = "London")
                }
                item("Postcode") {
                    ShippingText(header = "Postcode:", value = "SW1")
                }
                item("Country") {
                    ShippingText(header = "Country:", value = "United Kingdom")
                }
            }

            ConfirmAction(confirmAction)
        }
    }

    @Composable
    private fun ShippingText(
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
    private fun ConfirmAction(action: () -> Unit, modifier: Modifier = Modifier) {
        Button(
            onClick = action,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text("Confirm Order")
        }
    }
}

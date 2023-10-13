package com.bumble.appyx.navigation.node.cartItems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.imageloader.ResourceImage
import com.bumble.appyx.navigation.node.cakes.model.Cake

@Composable
fun CartContent(
    cartItems: Map<Cake, Int>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
    ) {
        item("Header") {
            Text(
                text = "Your items",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }
        val cartList = cartItems.map { it.key to it.value }
        item("Spacer" ) { Spacer(modifier = Modifier.requiredHeight(8.dp)) }
        items(
            count = cartList.size,
            key = { index -> cartList[index].first }
        ) { index ->
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            CartListItem(cartList[index])
        }
    }
}

@Composable
private fun CartListItem(cakeToQuantity: Pair<Cake, Int>) {
    val cake = cakeToQuantity.first
    val quantity = cakeToQuantity.second
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ResourceImage(
                path = cake.image,
                modifier = Modifier.width(50.dp).height(50.dp),
                contentScale = ContentScale.FillWidth,
                contentDescription = cake.name
            )
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = cake.name,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Quantity: $quantity",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
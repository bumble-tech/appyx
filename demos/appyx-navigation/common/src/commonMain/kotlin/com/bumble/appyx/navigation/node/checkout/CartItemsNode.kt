package com.bumble.appyx.navigation.node.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.navigator.LocalNavigator
import com.bumble.appyx.navigation.navigator.Navigator
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.cakes.Cake
import com.bumble.appyx.navigation.node.cart.Cart

class CartItemsNode(
    buildContext: BuildContext,
    private val cart: Cart,
    private val onCheckout: () -> Unit,
) : Node(
    buildContext = buildContext,
) {

    @Composable
    override fun View(modifier: Modifier) {
        val cartItems = cart.items.collectAsState(emptyMap())
        val navigator = LocalNavigator.current

        val onClearCart = { cart.clear() }
        val onGoToCake: (Cake) -> Unit = { navigator.goToCake(it) }

        Surface {
            if (cartItems.value.isEmpty()) {
                CartEmptyContent(navigator)
            } else {
                CartContent(
                    cartItems = cartItems.value,
                    onClearCart = onClearCart,
                    onCheckout = onCheckout,
                    onGoToCake = onGoToCake
                )
            }
        }
    }

    @Composable
    private fun CartEmptyContent(navigator: Navigator) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Your cart is empty",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.requiredHeight(16.dp))
            Button(onClick = {
                navigator.goToCakes()
            }) {
                Text("Get some cakes!")
            }
        }
    }
}


package com.bumble.appyx.navigation.node.checkout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection.EndToStart
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.node.cakes.Cake
import com.bumble.appyx.navigation.ui.EmbeddableResourceImage
import com.bumble.appyx.navigation.ui.md_red_200
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CartContent(
    cartItems: Map<Cake, Int>,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit,
    onGoToCake: (Cake) -> Unit,
    onPlusOneCake: (Cake) -> Unit,
    onMinusOneCake: (Cake) -> Unit,
    onDeleteCake: (Cake) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        val cartList = remember(cartItems) { cartItems.toList() }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .weight(1f),
        ) {
            item("Header") {
                Text(
                    text = "Your items",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
            item("Spacer") {
                Spacer(modifier = Modifier.requiredHeight(8.dp))
            }
            items(
                count = cartList.size,
                key = { index -> cartList[index].first }
            ) { index ->
                Spacer(modifier = Modifier.requiredHeight(8.dp))
                CartListItem(
                    cakeToQuantity = cartList[index],
                    onGoToCake = onGoToCake,
                    onPlusOneCake = onPlusOneCake,
                    onMinusOneCake = onMinusOneCake,
                    onDeleteCake = onDeleteCake,
                )
            }
            item("Actions") {
                CartActions(onClearCart, onCheckout)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.CartListItem(
    cakeToQuantity: Pair<Cake, Int>,
    onGoToCake: (Cake) -> Unit,
    onPlusOneCake: (Cake) -> Unit,
    onMinusOneCake: (Cake) -> Unit,
    onDeleteCake: (Cake) -> Unit,
) {
    val cake = cakeToQuantity.first
    val quantity = cakeToQuantity.second
    val coroutineScope = rememberCoroutineScope()

    val dismissState = rememberDismissState(confirmValueChange = { dismissValue ->
        when (dismissValue) {
            DismissValue.Default,
            DismissValue.DismissedToEnd -> false

            DismissValue.DismissedToStart -> {
                coroutineScope.launch {
                    delay(250)
                    onDeleteCake(cake)
                }
                true
            }
        }
    })

    SwipeToDismiss(
        modifier = Modifier.animateItemPlacement(),
        state = dismissState,
        directions = setOf(EndToStart),
        background = { CartItemDismissBackground() },
        dismissContent = {
            CardItem(
                cake = cake,
                quantity = quantity,
                onGoToCake = onGoToCake,
                plusOneCakeAction = onPlusOneCake,
                minusOneCakeAction = onMinusOneCake,
            )
        },
    )
}

@Composable
private fun LazyItemScope.CartItemDismissBackground() {
    Card {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillParentMaxHeight()
                .background(md_red_200)
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                tint = Color.Black,
                contentDescription = "Delete Cake",
            )
        }
    }
}

@Composable
private fun CardItem(
    cake: Cake,
    quantity: Int,
    onGoToCake: (Cake) -> Unit,
    plusOneCakeAction: (Cake) -> Unit,
    minusOneCakeAction: (Cake) -> Unit,
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(50.dp)) {
                EmbeddableResourceImage(
                    path = cake.image,
                    modifier = Modifier
                        .clickable(
                            onClick = { onGoToCake.invoke(cake) },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = cake.name
                )
            }
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = cake.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ActionButton(text = "-", cake = cake, action = minusOneCakeAction)
                Text(
                    modifier = Modifier.defaultMinSize(minWidth = 18.dp),
                    text = "$quantity",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
                ActionButton(text = "+", cake = cake, action = plusOneCakeAction)
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    cake: Cake,
    action: (Cake) -> Unit,
) {
    Button(
        modifier = Modifier.size(24.dp),
        onClick = { action(cake) },
        contentPadding = PaddingValues(0.dp),
        shape = CircleShape,
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CartActions(
    onClearCart: () -> Unit,
    onCheckout: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Button(
            onClick = onClearCart,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Clear cart")
        }
        Spacer(modifier = Modifier.requiredWidth(4.dp))
        Button(
            onClick = onCheckout,
            modifier = Modifier.weight(1f)
        ) {
            Text("Checkout")
        }
    }
}

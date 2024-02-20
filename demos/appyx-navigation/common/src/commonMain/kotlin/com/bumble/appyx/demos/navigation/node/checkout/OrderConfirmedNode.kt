package com.bumble.appyx.demos.navigation.node.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.LeafNode
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class OrderConfirmedNode(
    nodeContext: NodeContext,
    private val postViewAction: () -> Unit,
) : LeafNode(
    nodeContext = nodeContext,
) {

    @Composable
    override fun Content(modifier: Modifier) {
        Surface {
            PaymentDetails(modifier)
        }
    }

    @Composable
    private fun PaymentDetails(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Order confirmed icon",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text = "Order confirmed!", style = MaterialTheme.typography.titleMedium)
            LaunchedEffect(Unit) {
                delay(3.seconds)
                postViewAction.invoke()
            }
        }
    }
}

package com.bumble.appyx.sample.navigtion.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.LocalIntegrationPoint

/**
 * This Composable demonstrates how to add Appyx into Jetpack Compose Navigation.
 */
@Composable
fun ComposeNavigationRoot() {
    Column(Modifier.fillMaxSize()) {
        Text(
            text = "Navigation Compose interop example",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            color = Color.Black,
            style = MaterialTheme.typography.subtitle1
        )

        val googleNavController = rememberNavController()
        NavHost(navController = googleNavController, startDestination = "google-route") {
            composable("google-route") {
                GoogleRoute { googleNavController.navigate("appyx-route") }
            }
            composable("appyx-route") {
                AppyxRoute { googleNavController.navigate("google-route") }
            }
        }
    }
}

@Composable
fun GoogleRoute(onAppyxNavigationClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Google's Jetpack Navigation screen")
        Button(onClick = { onAppyxNavigationClick() }) {
            Text("Navigate to Appyx")
        }
    }
}


@Composable
fun AppyxRoute(onGoogleNavigationClick: () -> Unit) {
    NodeHost(integrationPoint = LocalIntegrationPoint.current) {
        ComposeNavigationContainerNode(
            buildContext = it,
            onGoogleNavigationClick = onGoogleNavigationClick
        )
    }
}

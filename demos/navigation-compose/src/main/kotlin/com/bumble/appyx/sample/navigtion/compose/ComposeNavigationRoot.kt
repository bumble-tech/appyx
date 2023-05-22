package com.bumble.appyx.sample.navigtion.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.integrationpoint.LocalIntegrationPoint
import com.bumble.appyx.navigation.platform.AndroidPlatformLifecycle

/**
 * This Composable demonstrates how to add Appyx into Jetpack Compose Navigation.
 */
@Composable
fun ComposeNavigationRoot(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Navigation Compose interop example",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge
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
internal fun GoogleRoute(modifier: Modifier = Modifier, onAppyxNavigationClick: () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Google's Jetpack Navigation screen")
        Button(onClick = { onAppyxNavigationClick() }) {
            Text("Navigate to Appyx")
        }
    }
}


@Composable
internal fun AppyxRoute(onGoogleNavigationClick: () -> Unit) {
    NodeHost(
        lifecycle = AndroidPlatformLifecycle(LocalLifecycleOwner.current.lifecycle),
        integrationPoint = LocalIntegrationPoint.current
    ) {
        ComposeNavigationContainerNode(
            buildContext = it,
            onGoogleNavigationClick = onGoogleNavigationClick
        )
    }
}

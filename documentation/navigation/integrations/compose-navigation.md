---
title: Appyx + Jetpack Compose Navigation
---

# Appyx + Jetpack Compose Navigation

You can easily make Appyx co-exist with Jetpack Compose Navigation. This might be useful if:

- You want to to use some [AppyxComponents](../../components/index.md) for navigation in your non-Appyx app.
- You want to migrate to Appyx gradually.

No special downloads are required.

## Sample

```kotlin
/**
 * This Composable demonstrates how to add Appyx into Jetpack Compose Navigation.
 */
@Composable
fun ComposeNavigationRoot(modifier: Modifier = Modifier) {
    val googleNavController = rememberNavController()
    NavHost(
        navController = googleNavController,
        startDestination = "google-route"
    ) {
        composable("google-route") {
            GoogleRoute { googleNavController.navigate("appyx-route") }
        }
        composable("appyx-route") {
            AppyxRoute { googleNavController.navigate("google-route") }
        }
    }
}

@Composable
internal fun GoogleRoute(
    modifier: Modifier = Modifier,
    onNavigateToAppyxRoute: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Compose Navigation screen")
        Button(onClick = { onNavigateToAppyxRoute() }) {
            Text("Navigate to Appyx")
        }
    }
}


@Composable
internal fun AppyxRoute(
    onNavigateToGoogleRoute: () -> Unit
) {
    NodeHost(integrationPoint = LocalIntegrationPoint.current) {
        RootNode(
            nodeContext = it,
            // use this in the Node / some child Node,
            // in business logic or in the view on some button:
            onNavigateToGoogleRoute = onNavigateToGoogleRoute 
        )
    }
}
```

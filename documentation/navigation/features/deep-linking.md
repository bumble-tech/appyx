---
title: Appyx Navigation – Deep linking
---

# Deep linking

Building on top of [explicit navigation](../concepts/explicit-navigation.md), implementing deep links is straightforward:

```kotlin
class ExplicitNavigationExampleActivity : NodeActivity(), Navigator {

    lateinit var rootNode: RootNode

    fun handleDeepLink(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            when {
                (it.data?.host == "onboarding") -> navigateToOnBoarding()
                else -> Unit
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NodeHost(integrationPoint = appyxIntegrationPoint) {
                RootNode(
                    nodeContext = it,
                    plugins = listOf(object : NodeReadyObserver<RootNode> {
                        override fun init(node: RootNode) {
                            rootNode = node
                            handleDeepLink(intent = intent)
                        }
                    })
                )
            }
        }
    }
    
    private fun navigateToOnBoarding() {
        // implement explicit navigation
    }
}
```

Check `ExplicitNavigationExampleActivity` in the samples to inspect the full code.



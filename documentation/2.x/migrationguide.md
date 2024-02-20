---
title: Appyx – 2.x Migration guide
---

# Appyx – Migration guide


## Project organisation

### 1.x
Packaged as a single library, implementing Model-driven navigation with transitions together.

### 2.x
The library is packaged as multiple artifacts.

#### :appyx-navigation

- Mostly analogous to Appyx 1.0’s functionality but without the transitions
- Cares about the Node structure & Android-related functionality
- Uses `:appyx-interactions` as a dependency to implement navigation using gestures and transitions
- Android library
- Compose Multiplatform. 

Check also [Multiplatform](../navigation/multiplatform.md) documentation and the `:demos:appyx-navigation` module for code examples.

#### :appyx-interactions

- Gesture-driven, state-based motion kit & transition engine
- Does not contain Node-related functionality → moved to `:appyx-navigation`
- Does not contain Android-specific functionality → moved to `:appyx-navigation`
- Compose Multiplatform
   
#### :appyx-components

- Pre-packaged components to use with `:appyx-navigation`
- Compose Multiplatform
 

## Rough equivalents

- 1.x → 2.x
- `NavModel` → `AppyxComponent`
- `TransitionHandler` → `Visualisation`
- `BuildContext` → `NodeContext`


## Migration guide

### Gradle

![Maven Central](https://img.shields.io/maven-central/v/com.bumble.appyx/appyx-interactions)

#### Core

Note that [BackStack](../components/backstack.md) and [Spotlight](../components/spotlight.md) are now standalone artifacts. Check your usage, you might only need `backstack`:

```diff
-implementation("com.bumble.appyx:core:1.x.x")
+implementation("com.bumble.appyx:appyx-navigation:2.0.0-alpha01")
+implementation("com.bumble.appyx:backstack-android:2.0.0-alpha01")
+implementation("com.bumble.appyx:spotlight-android:2.0.0-alpha01")
```



#### Testing

Artifacts have a `utils-` prefix:

```diff
-implementation("com.bumble.appyx:testing-ui")
-implementation("com.bumble.appyx:testing-unit-common")
-implementation("com.bumble.appyx:testing-junit4")
-implementation("com.bumble.appyx:testing-junit5")

+implementation("com.bumble.appyx:utils-testing-ui")
+implementation("com.bumble.appyx:utils-testing-unit-common")
+implementation("com.bumble.appyx:utils-testing-junit4")
+implementation("com.bumble.appyx:utils-testing-junit5")
```


### Usage site (RootNode)

```diff
-import com.bumble.appyx.core.composable.Children
-import com.bumble.appyx.core.modality.BuildContext
-import com.bumble.appyx.core.node.Node
-import com.bumble.appyx.core.node.ParentNode
-import com.bumble.appyx.navmodel.backstack.BackStack
-import com.bumble.appyx.navmodel.backstack.operation.push
-import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader

+import com.bumble.appyx.components.backstack.BackStack
+import com.bumble.appyx.components.backstack.BackStackModel
+import com.bumble.appyx.components.backstack.operation.push
+import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
+import com.bumble.appyx.navigation.composable.AppyxNavigationContainer
+import com.bumble.appyx.navigation.modality.NodeContext
+import com.bumble.appyx.navigation.node.Node
+import com.bumble.appyx.navigation.node.ParentNode

class RootNode(
-    buildContext: BuildContext,
+    nodeContext: NodeContext,
    private val backStack: BackStack<NavTarget> = BackStack(
-        initialElement = Child1,
-        savedStateMap = buildContext.savedStateMap
+        model = BackStackModel(
+            initialTarget = Child1,
+            savedStateMap = nodeContext.savedStateMap
+        ),
+        visualisation = { BackStackFader(it) },
     ),
 ) : ParentNode<RootNode.NavTarget>(
-    navModel = backStack,
+    appyxComponent = backStack,
-     buildContext = buildContext
+     nodeContext = nodeContext
 ) {

    sealed class NavTarget {
        object Child1 : NavTarget()
        object Child2 : NavTarget()
    }

-    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
-        when (navTarget) {
-            is Child1 -> Child1Node(buildContext) { backStack.push(Child2) }
-            is Child2 -> Child2Node(buildContext)
+    override fun buildChildNode(reference: NavTarget, nodeContext: NodeContext): Node =
+        when (reference) {
+            is Child1 -> Child1Node(nodeContext) { backStack.push(Child2) }
+            is Child2 -> Child2Node(nodeContext)
        }

    @Composable
    override fun Content(modifier: Modifier) {
-        Children(
-            navModel = backStack,
-            transitionHandler = rememberBackstackFader(transitionSpec = { spring() }),
+        AppyxNavigationContainer(
+            appyxComponent = backStack,
            modifier = Modifier.fillMaxSize()
        )
    }
}
```


### Usage site (@Preview)

```diff
-import com.bumble.appyx.core.integration.NodeHost
-import com.bumble.appyx.core.integrationpoint.IntegrationPointStub

+import com.bumble.appyx.navigation.integration.NodeHost
+import com.bumble.appyx.navigation.integration.IntegrationPointStub

@Preview
@Composable
fun RootNodePreview() {
    Box(Modifier.fillMaxSize()) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            RootNode(
                nodeContext = NodeContext.root(null)
            )
        }
    }
}
```

If you have a broken import that's not listed above, please open an issue/PR, or let us know on the **#appyx** channel on Kotlinlang Slack. Thanks!

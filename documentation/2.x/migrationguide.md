# Appyx

## 2.x vs 1.x project organisation

### Appyx 1.0

Packaged as a single library, implementing Model-driven navigation with transitions together.

### Appyx 2.0

The library is packaged as multiple artifacts.

#### :appyx-navigation

- Mostly analogous to Appyx 1.0’s functionality but without the transitions
- Cares about the Node structure & Android-related functionality
- Uses `:appyx-interactions` as a dependency to implement navigation using gestures and transitions
- Android library
- Supports Compose multiplatform. WebNodeHost and DesktopNodeHost provide convenience wrappers for
  the NodeHost entry point and the demos/appyx-navigation module demonstrates their usage.

#### :appyx-interactions

- Gesture-driven, state-based motion kit & transition engine
- Does not contain Node-related functionality → moved to `:appyx-navigation`
- Does not contain Android-specific functionality → moved to `:appyx-navigation`
- Compose multiplatform

#### :appyx-components

- Pre-packaged components to use with `:appyx-navigation`
- Compose multiplatform

## 1.x ~ 2.x rough equivalents

- `NavModel` -> `AppyxComponent`
- `TransitionHandler` -> `MotionController`

## 1.x → 2.x Migration guide

### Gradle

![Maven Central](https://img.shields.io/maven-central/v/com.bumble.appyx/appyx-interactions)

#### Core

Note that [BackStack](../components/backstack.md) and [Spotlight](../components/spotlight.md) are
now standalone artifacts. Check your usage, you might only need `backstack`:

```diff
-    implementation("com.bumble.appyx:core:1.x.x")
+    implementation("com.bumble.appyx:appyx-navigation:2.0.0-alpha01")
+    implementation("com.bumble.appyx:backstack-android:2.0.0-alpha01")
+    implementation("com.bumble.appyx:spotlight-android:2.0.0-alpha01")
```

#### Testing

Artifacts have a `utils-` prefix:

```diff
-"com.bumble.appyx:testing-ui"
-"com.bumble.appyx:testing-unit-common"
-"com.bumble.appyx:testing-junit4"
-"com.bumble.appyx:testing-junit5"

+"com.bumble.appyx:utils-testing-ui"
+"com.bumble.appyx:utils-testing-unit-common"
+"com.bumble.appyx:utils-testing-junit4"
+"com.bumble.appyx:utils-testing-junit5"
```

### MainActivity

```diff
-import com.bumble.appyx.core.integration.NodeHost
-import com.bumble.appyx.core.integrationpoint.NodeActivity

+import com.bumble.appyx.navigation.integration.NodeHost
+import com.bumble.appyx.navigation.integrationpoint.NodeActivity

    class MainActivity : NodeActivity() {
         super.onCreate(savedInstanceState)
         setContent {
             HelloAppyxTheme {
-                NodeHost(integrationPoint = appyxIntegrationPoint) {
+                NodeHost(integrationPoint = appyxV2IntegrationPoint) {
                     RootNode(it)
                 }
             }
     }
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
+import com.bumble.appyx.navigation.composable.AppyxComponent
+import com.bumble.appyx.navigation.modality.BuildContext
+import com.bumble.appyx.navigation.node.Node
+import com.bumble.appyx.navigation.node.ParentNode

class RootNode(
    buildContext: BuildContext,
         private val backStack: BackStack<NavTarget> = BackStack(
-        initialElement = Child1,
-        savedStateMap = buildContext.savedStateMap
+        model = BackStackModel(
+            initialTarget = Child1,
+            savedStateMap = buildContext.savedStateMap
+        ),
+        motionController = { BackStackFader(it) },
     ),
 ) : ParentNode<RootNode.NavTarget>(
-    navModel = backStack,
+    appyxComponent = backStack,
     buildContext = buildContext
 ) {

    sealed class NavTarget {
        object Child1 : NavTarget()
        object Child2 : NavTarget()
    }

-    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
-        when (navTarget) {
+    override fun resolve(interactionTarget: NavTarget, buildContext: BuildContext): Node =
+        when (interactionTarget) {
            is Child1 -> Child1Node(buildContext) { backStack.push(Child2) }
            is Child2 -> Child2Node(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
-        Children(
-            navModel = backStack,
-            transitionHandler = rememberBackstackFader(transitionSpec = { spring() }),
+        AppyxComponent(
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
+import com.bumble.appyx.navigation.integrationpoint.IntegrationPointStub

@Preview
@Composable
fun RootNodePreview() {
    Box(Modifier.fillMaxSize()) {
        NodeHost(integrationPoint = IntegrationPointStub()) {
            RootNode(
                buildContext = BuildContext.root(null)
            )
        }
    }
}
```

If you have a broken import that's not listed above, please open an issue/PR, or let us know on
the **#appyx** channel on Kotlinlang Slack. Thanks!

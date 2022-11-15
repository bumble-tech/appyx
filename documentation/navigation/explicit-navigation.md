# Explicit navigation

[In this section](https://bumble-tech.github.io/appyx/navigation/composable-navigation/#navigation-in-the-tree) we covered how changing `NavModel` will result in manipulating
the Appyx tree on the local level. 

But there are use cases when instead of the local changes on any level of the Appyx tree we want to switch the navigation state globally. 

For instance, user wants to navigate from `Chat`  

<img src="https://imgur.com/a/nIB7qMY" width="450">

to onboarding `O1` node explicitly by calling a function:

<img src="https://imgur.com/a/o7zEscw" width="450">

To explicitly navigate to any given `Node`, we need to determine the path which leads from the root of the tree to that `Node`. Once we've done that,
starting from the top of the tree we attach the next `Node` from the determined path. Repeat this step until we reach the desired `Node`.   

In our example, we start from the `Root` node, attach `Onboarding` Node to `Root`, and then we attach `O1` to `Onboarding`.
To iteratively perform `Node` attachment Appyx provides `attachChild` API.

## Attach child API

Let's take a look at our example and implement navigation to `O1` using `attachChild` API.

First, we need to define how to programmatically attach `Onboarding` to the `Root`:

```kotlin
class RootNode(
    buildContext: BuildContext,
    backStack: BackStack<NavTarget>
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    navModel = backStack,
) {
    
    suspend fun attachOnboarding(): OnboardingNode {
        return attachChild {
            backStack.replace(NavTarget.Onboarding)
        }
    }
}
```

Let's break down what happens here:
1. `attachChild` is provided with a lambda where we add `NavTarget.Onboarding` to a `BackStack`.
2. `attachChild` internally executes this lambda and waits for the provided `OnboardingNode` node to appear in the children of `Root` node after.
3. Once the desired `Node` appeared in the children list `attachChild` returns it.    
In the case when you provide an action which will not result in appearing the desired `Node` in the children list, for instance:

```kotlin
suspend fun attachOnboarding(): OnboardingNode {
    return attachChild {
        backStack.replace(NavTarget.Main)
    }
}
```

exception will be thrown after a timeout.


Unlike `Root`, `Onboarding` uses `Spotlight` instead of `BackStack` as a `NavModel`, so navigation to `O1` will be slightly different:  

```kotlin
class OnboardingNode(
    buildContext: BuildContext,
    spotlight: Spotlight<NavTarget>
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    navModel = spotlight,
) {

    suspend fun attachO1(): O1Node {
        return attachChild {
            spotlight.activate(index = 0)
        }
    }
}
```

The next step is to obtain the reference to a root of the Appyx tree in the hosting `Actvity` using `NodeReadyObserver` plugin:


```kotlin
class ExplicitNavigationExampleActivity : NodeActivity() {

    lateinit var rootNode: RootNode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NodeHost(integrationPoint = appyxIntegrationPoint) {
                RootNode(
                    buildContext = it,
                    plugins = listOf(object : NodeReadyObserver<RootNode> {
                        override fun init(node: RootNode) {
                            rootNode = node
                        }
                    })
                )
            }
        }
    }
}
```

Once we implemented navigation to `OnboardingNode` and `O1`, and grabbed the reference to the root of a tree, 
we can execute the chain of actions which will result in navigation to `O1`:

```kotlin
class ExplicitNavigationExampleActivity : NodeActivity() {

     fun navigateToO1() {
         lifecycleScope.launch {
             rootNode
                 .attachOnboarding()
                 .attachO1()
         }
     }
}
```

As the last step, we can define our navigation method in the interface `Navigator`, let the `Activity` implement it,
and provide the instance of a `Navigator` down the Appyx tree:

```kotlin
interface Navigator {
     fun navigateToO1()
}
```

```kotlin
class ExplicitNavigationExampleActivity : NodeActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NodeHost(integrationPoint = appyxIntegrationPoint) {
                RootNode(
                    buildContext = it,
                    navigator = this@ExplicitNavigationExampleActivity,
                    plugins = listOf(object : NodeReadyObserver<RootNode> {
                        override fun init(node: RootNode) {
                            rootNode = node
                        }
                    })
                )
            }
        }
    }
}
```


Calling `Navigator` methods explicitly from inside of the Appyx tree will change the global navigation state.

## Wait for child attached API

In the example above we learned how to navigate explicitly in Appyx.

But there are cases when we want to wait for a certain action to be performed by a user, and only then
execute logic.

Let's imagine the following example:

<img src="https://imgur.com/a/RIxF6Q6" width="450">

1. User is logged in and uses the application.
2. Once user is logged out (`LoggedOutNode` is attached to `RootNode`) we need to show a `PromoNode`.

To implement that behaviour we need to use `waitForChildAttached` API: 

```kotlin
class RootNode(
    buildContext: BuildContext,
) : ParentNode<NavTarget>(
    buildContext = buildContext
) {
    
    suspend fun waitForLoggedOutAttached() =  waitForChildAttached<LoggedOutNode>()
}
```

This method will wait for `LoggedOutNode` to appear in the child list of `RootNode` and return `LoggedOutNode`.
In the section above we covered how we can explicitly navigate to `PromoNode` from `LoggedOutNode`,
so the final solution to implement the desired behaviour could look like:

```kotlin
class Activity : NodeActivity() {

     fun showPromoWhenLoggedOut() {
         lifecycleScope.launch {
             rootNode
                 .waitForLoggedOutAttached()
                 .attachPromo()
         }
     }
}
```


To check the actual code please visit `ExplicitNavigationExampleActivity` in our samples.

# Explicit navigation

When [Implicit navigation](implicit-navigation.md) doesn't fit your use case, you can try an explicit approach.

!!! info "Relevant methods"

    - ParentNode.attachChild()
    - ParentNode.waitForChildAttached()

Using these methods we can chain together a path which leads from the root of the tree to a specific `Node`.

## Use case

We want to navigate from `Chat`

<img src="https://i.imgur.com/jqWOHhJ.png" width="450">

to onboarding's first screen `O1`:

<img src="https://i.imgur.com/MWgLOWy.png" width="450">

This time we'll want to do this explicitly by calling a function.


## The plan 

1. Create a public method on `Root` that attaches `Onboarding`
2. Create a public method on `Onboarding` that attaches the first onboarding screen
3. Create a `Navigator`, that starting from an instance of `Root`, can chain these public methods together into a single action: `navigateToO1()`
4. Capture an instance of `Root` to use with `Navigator`
5. Call `navigateToO1()` on our `Navigator` instance


## Step 1 – `Root` → `Onboarding`

First, we need to define how to programmatically attach `Onboarding` to the `Root`:

```kotlin
class RootNode(
    buildContext: BuildContext,
    backStack: BackStack<NavTarget>
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = backStack,
) {
    
    suspend fun attachOnboarding(): OnboardingNode {
        return attachChild {
            backStack.replace(NavTarget.Onboarding)
        }
    }
}
```

Let's break down what happens here:

1. Since `attachChild` has a generic `<T>` return type, it will conform to the defined `OnboardingNode` type 
2. However, `attachChild` doesn't know how to create navigation to `OnboardingNode` – that's something only we can do with the provided lambda
3. We replace `NavTarget.Onboarding` into the back stack
4. Doing this _should_ result in `OnboardingNode` being created and added to `RootNode` as a child 
5. `attachChild` expects an instance of `OnboardingNode` to appear as a child of `Root` as a consequence of executing our lambda
6. Once it appears, `attachChild` returns it


!!! info "Important"

    It's our responsibility to make sure that the provided lambda actually results in the expected child being added. If we accidentally do something else instead, for example:

    ```kotlin
    suspend fun attachOnboarding(): OnboardingNode {
        return attachChild {
            backStack.replace(NavTarget.Main) // Wrong NavTarget
        }
    }
    ```
    
    Then an exception will be thrown after a timeout.


## Step 2 – `Onboarding` → `O1`

Unlike `Root`, `Onboarding` uses [Spotlight](../../components/spotlight.md) instead of [BackStack](../../components/backstack.md) as an `AppyxComponent`, so navigation to the first screen is slightly different:  

```kotlin
class OnboardingNode(
    buildContext: BuildContext,
    spotlight: Spotlight<NavTarget>
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = spotlight,
) {

    suspend fun attachO1(): O1Node {
        return attachChild {
            spotlight.activate(index = 0)
        }
    }
}
```


## Step 3 – Our `Navigator`

```kotlin
interface Navigator {
     fun navigateToO1()
}
```

In this case we'll implement it directly with our activity:

```kotlin
class ExplicitNavigationExampleActivity : NodeActivity(), Navigator {

    lateinit var rootNode: RootNode // See the next step

     override fun navigateToO1() {
         lifecycleScope.launch {
             rootNode
                 .attachOnboarding()
                 .attachO1()
         }
     }
}
```

## Step 4 – An instance of `RootNode`

As the last piece of the puzzle, we'll also need to capture the instance of `RootNode` to make it all work. We can do that by a `NodeReadyObserver` plugin when setting up our tree:


```kotlin
class ExplicitNavigationExampleActivity : NodeActivity(), Navigator {

    lateinit var rootNode: RootNode

    override fun navigateToO1() { /*...*/ }

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

## Step 5 – Using the `Navigator`

See how in the previous snippet `RootNode` receives a `navigator` dependency. 

It can pass it further down the tree as a dependency to other nodes. Those nodes can call the methods of the `Navigator`, which will change the global navigation state directly.


---

## Bonus: Wait for a child to be attached

There might be cases when we want to wait for a certain action to be _performed by the user_, rather than us, to result in a child being attached.

In these cases we can use `ParentNode.waitForChildAttached()` instead.


### Use case – Wait for login

A typical case building an explicit navigation chain that relies on `Logged in` being attached. Most probably `Logged in` has a dependency on some kind of a `User` object. Here we want to wait for the user to authenticate themselves, rather than creating a dummy user object ourselves.


```kotlin
class RootNode(
    buildContext: BuildContext,
) : ParentNode<NavTarget>(
    buildContext = buildContext
) {
    
    suspend fun waitForLoggedIn(): LoggedInNode = 
        waitForChildAttached<LoggedInNode>()
}
```

This method will wait for `LoggedInNode` to appear in the child list of `RootNode` and return with it. If it's already there, it returns immediately.

A navigation chain using it could look like:

```kotlin
class ExplicitNavigationExampleActivity : NodeActivity(), Navigator {

     override fun navigateToProfile() {
         lifecycleScope.launch {
             rootNode
                 .waitForLoggedIn()
                 .attachMain()
                 .attachProfile()
         }
     }
}
```

You can find related code examples in `ExplicitNavigationExampleActivity` in our samples.

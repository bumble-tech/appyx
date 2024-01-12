---
title: Appyx Navigation – Plugins
---

# Plugins

## Keeping extra concerns out of Node

```Nodes``` are meant to be simple structural elements, and should be kept lean.

To keep the framework agnostic of any specific approach / pattern you want to use, there aren't any fixed parts. Rather, the ```Node``` offers an extension point using ```Plugins``` in its constructor:

```kotlin
abstract class Node(
    nodeContext: NodeContext,
    val view: NodeView = EmptyNodeView,
    plugins: List<Plugin> = emptyList() // <--
)
```

So what is a ```Plugin```?

A ```Plugin``` is an empty interface extended by many actual ones:

```kotlin
interface Plugin

```

## Plugins

### Lifecycle-related plugins

```kotlin
interface NodeLifecycleAware : Plugin {
    fun onCreate(lifecycle: Lifecycle) {}
}

fun interface Destroyable : Plugin {
    fun destroy()
}
```


### Component level plugins

Sometimes you need to grab a reference to the component as a whole, either as an interface, or its implementation, the ```Node```.

This will come especially handy when working with workflows.


```kotlin
interface NodeAware : Plugin {
    val node: Node<*>

    fun init(node: Node<*>) {}
}
```

There are helper classes found in the library, so you don't have to implement the above interfaces, you can just use delegation:

```kotlin
class SomeClass(
    private val nodeAware: NodeAware = NodeAwareImpl()
) : NodeAware by nodeAware {

    fun foo() {
        // [node] is an automatically available property coming from the NodeAware interface
        // the reference is automatically set for you by the framework + the NodeAwareImpl class
        // so you can use it right away:
        node.doSomething()
    }
}
```

⚠️ Note: the reference to `node` is set by `Node` automatically, and isn't available immediately after constructing your object, but only after the construction of the `Node` itself.


### Navigation plugins

In case if you need to control navigation behaviour, you can use these plugins:

```kotlin
interface UpNavigationHandler : Plugin {
    fun handleUpNavigation(): Boolean = false
}

interface BackPressHandler : Plugin {
    val onBackPressedCallback: OnBackPressedCallback? get() = null
}
```

`UpNavigationHandler` controls `Node.navigateUp` behaviour and allows to intercept its invocation.

`BackPressHandler` controls device back press behaviour via `androidx.activity.OnBackPressedCallback`.
You can read more about it [here](https://developer.android.com/guide/navigation/navigation-custom-back).

⚠️ Note: `OnBackPressedCallback` are invoked in the following order:
1. From children to parents. Render order of children matters! The last rendered child will be the first to handle back press.
2. Direct order of plugins within a node. Plugins are invoked in order they appears in `Node(plugins = ...)` before the AppyxComponent. 


## Using Plugins 

All plugins are designed to have empty `{}` default implementations (or other sensible defaults when a return value is defined), so it's convenient to implement them only if you need.

Don't forget to pass your `Plugins` to your `Node`:

```kotlin
internal class MyNode(
    // ...
    plugins: List<Plugins> = emptyList()
    // ...
) : Node<Nothing>(
    // ...
    plugins = plugins
    // ...
)
```

⚠️ Note: `plugins` is a `List`, as the order matters here. All `Plugin` instances are invoked in the order they appear in the list.

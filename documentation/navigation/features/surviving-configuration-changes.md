---
title: Appyx Navigation – Surviving configuration changes
---

# Surviving configuration changes

To retain objects during configuration change you can use the `RetainedInstanceStore` class.

## How does it work?

{==

The `RetainedInstanceStore` stores objects within a singleton.

Every `Node` has access to the `RetainedInstanceStore` and manages these cases automatically:

- The `Activity` is recreated: the retained instance is returned instead of a new instance.
- The `Activity` is destroyed: the retained instance is removed and disposed.

==}

## Example

Appyx provides extension methods on the `NodeContext` class (an instance which is passed to every `Node` upon creation) to access the `RetainedInstanceStore`. You can use these to:

- Create your retained objects (`factory`)
- Define cleanup mechanisms (`disposer`) to be run when the retained object will be removed on `Activity` destroy.

You can opt to use the `Builder` pattern to provide dependencies to your `Node` and separate this logic:

{==

Note: to use the rx2/rx3 `getRetainedDisposable` extension methods you see below, you need to add the relevant gradle dependencies. Please refer to [Downloads](../../releases/downloads.md).

==}

```kotlin
import com.bumble.appyx.navigation.builder.Builder
import com.bumble.appyx.navigation.modality.NodeContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.store.getRetainedInstance
import com.bumble.appyx.utils.interop.rx2.store.getRetainedDisposable

class YourNodeBuilder : Builder<YourPayload>() {

    override fun build(nodeContext: NodeContext, payload: YourPayload): Node {
        // Case 1:
        // If your type implements an rx2/rx3 Disposable,
        // you don't need to pass a disposer:
        val retainedFoo = nodeContext.getRetainedDisposable {
            Foo(payload)
        }
        
        // Case 2:
        // If your type doesn't implement an rx2/rx3 Disposable,
        // you can define a custom cleanup mechanism:
        val retainedFoo = nodeContext.getRetainedInstance(
            factory = { Foo(payload) },
            disposer = { it.cleanup() } // it: Foo
        )
        
        val view = YourNodeViewImpl()

        return YourNode(
            nodeContext = nodeContext,
            foo = retainedFoo, 
            view = view,
        )
    }
}
```

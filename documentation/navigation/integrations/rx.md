---
title: Appyx + RxJava
---

# Appyx + RxJava

Rx2 and Rx3 implementations are provided for the functionality below. Please refer to [Downloads](../../releases/downloads.md) for gradle artifacts.


## Connectable

You can use this together with the [ChildAware API](../features/childaware.md) to set up communication between Nodes in a decoupled way:

```kotlin
interface Connectable<Input, Output> : NodeLifecycleAware {
    val input: Relay<Input>
    val output: Relay<Output>
}
```

## RetainedInstanceStore

Provides a singleton store to survive configuration changes. The rx2/rx3 helpers add automatic disposal on objects implementing `Disposable`.

You can find more details here: [Surviving configuration change](../features/surviving-configuration-changes.md) 

```kotlin
import com.bumble.appyx.utils.interop.rx2.store.getRetainedDisposable

class YourNodeBuilder : Builder<YourPayload>() {

    override fun build(nodeContext: NodeContext, payload: YourPayload): Node {
        
        // If your type implements an rx2/rx3 Disposable,
        // you don't need to pass a disposer:
        val retainedFoo = nodeContext.getRetainedDisposable {
            Foo(payload)
        }

        return YourNode(
            nodeContext = nodeContext,
            foo = retainedFoo, 
            view = view,
        )
    }
}
```


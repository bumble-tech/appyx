# ChildAware API

The framework includes the `ChildAware` interface which comes with a powerful API.

It allows you to scope communication with (or between) dynamically available child nodes easily.

## Baseline

In the next examples:

1. Let's assume that `SomeNode` can host multiple child nodes: `Child1`, `Child2`, etc.
2. `SomeInteractor` belongs to `SomeNode` and is passed as a [Plugin](plugins.md)
   to it
3. `SomeInteractor` extends the `Interactor` helper class from the framework:
    - It implements `NodeLifecycleAware`, which makes sure it will receive the `onCreate` callback
      from the framework
    - It implements `ChildAware`, which unlocks the DSL we'll see in the following
      snippets

## Single child scenario

```kotlin
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.children.whenChildAttached
import com.bumble.appyx.core.children.whenChildrenAttached
import com.bumble.appyx.core.clienthelper.interactor.Interactor


class SomeInteractor : Interactor<SomeNode>() {

    override fun onCreate(lifecycle: Lifecycle) {
        lifecycle.subscribe(onCreate = {
            
            // This lambda is executed every time a node of type Child1Node is attached:
            whenChildAttached { commonLifecycle: Lifecycle, child1: Child1Node ->
                // TODO:
                //  - establish communication with child1 
                //  - use commonLifecycle for scoping 
                //  - it will be capped by the lifecycles of child1 and the parent
            }
        })
    }
}
```


## Multiple children

```kotlin
import androidx.lifecycle.Lifecycle
import com.bumble.appyx.core.children.whenChildAttached
import com.bumble.appyx.core.children.whenChildrenAttached
import com.bumble.appyx.core.clienthelper.interactor.Interactor


class SomeInteractor : Interactor<SomeNode>() {

    override fun onCreate(lifecycle: Lifecycle) {
        lifecycle.subscribe(onCreate = {

            // This lambda is executed every time these two nodes are attached at the same time:
            whenChildrenAttached { commonLifecycle: Lifecycle, child1: Child1Node, child2: Child2Node ->
                // TODO
                //  - establish communication between child1 & child2 
                //  - use commonLifecycle for scoping
                //  - it will be capped by the lifecycles of child1, child2 and the parent
            }
        })
    }
}
```


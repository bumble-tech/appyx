# Configuration change

To retain objects during configuration change you can use the `RetainedInstanceStore` class.

## How does it work?

The `RetainedInstanceStore` stores the objects within a singleton. The node manages whether the content should be removed by checking whether the `Activity` is being recreated due to a configuration change or not.

These are the following scenarios:
- If the `Activity` is recreated: the retained instance is returned instead of a new instance.
- If the `Activity` is destroyed: the retained instance is removed and disposed.

## Example

Here is an example of how you can use the `RetainedInstanceStore`:

```kotlin
import com.bumble.appyx.core.builder.Builder
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.store.getRetainedInstance
import com.bumble.appyx.interop.rx2.store.getRetainedDisposable

class RetainedInstancesBuilder : Builder<String>() {

    override fun build(buildContext: BuildContext, payload: String): Node {
        val retainedNonDisposable = buildContext.getRetainedInstance(
            factory = { NonDisposableClass(payload) },
            disposer = { feature.cleanUp() }
        ) 
        val retainedFeature = buildContext.getRetainedDisposable {
            RetainedInstancesFeature(payload)
        }

        val view = RetainedInstancesViewImpl()
        val interactor = RetainedInstancesInteractor(
            feature = retainedFeature,
            nonDisposable = retainedNonDisposable,
            view = view
        )

        return RetainedInstancesNode(
            buildContext = buildContext,
            view = view,
            plugins = listOf(interactor)
        )
    }
}
```

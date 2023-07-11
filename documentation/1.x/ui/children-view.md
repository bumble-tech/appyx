{% include-markdown "../deprecation.md" %}

# Adding children to the view

Navigation models define only the abstract model, not how that model will look on the screen. This section describes different ways of adding children (navigation targets) to the composition.

All the below mentioned composables should be added to the `View` of the parent node.


## Children

Renders all visible children of a NavModel. This is the simplest and most common case.

```kotlin
@Composable
override fun View(modifier: Modifier) {
    Children(
        modifier = Modifier, // optional
        navModel = TODO(),
        transitionHandler = TODO() // optional
    )
}
```

When rendering children you can have access to `TransitionDescriptor` which provides the following information:

```kotlin
@Immutable
data class TransitionDescriptor<NavTarget, out State>(
    val params: TransitionParams,
    val operation: Operation<NavTarget, out State>,
    val element: NavTarget,
    val fromState: State,
    val toState: State
)
```

Additionally, you can supply custom modifier to a child `Node`. In this example, we're supplying different `Modifier`
to a child `Node` depending on the `NavTarget`: 

```kotlin
@Composable
override fun View(modifier: Modifier) {
    Children(
        modifier = Modifier, // optional
        navModel = TODO(),
        transitionHandler = TODO() // optional
    ) {
        children<NavTarget> { child, descriptor ->
            child(
                modifier = Modifier
                    .background(
                        color = getBackgroundColor(descriptor.element)
                    )
            )
        }
    }
}
```

## Child

Renders a single child associated to a `NavElement`. Useful if you want to define different child placements in the layout individually. 

```kotlin
@Composable
override fun View(modifier: Modifier) {
    Child(
        navElement = element,
        transitionHandler = TODO()
    ) { child, _ ->
        // TODO wrap in your own composables
        child()
    }
}
```

## Lazy lists / grids

```kotlin
@Composable
override fun View(modifier: Modifier) {
    // TODO grab all visible children from the navModel manually
    val children by navModel.visibleChildrenAsState()
    GridExample(children)
}

@Composable
private fun GridExample(elements: List<NavElement<NavTarget, out Any?>>) {
    LazyVerticalGrid(
        columns = Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(elements) { element ->
            // TODO use Child composable to render them individually inside the list / grid
            Child(navElement = element)
        }
    }
}
```


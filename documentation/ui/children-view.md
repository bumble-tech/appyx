# Adding children to the view

Routing sources define the model of dealing with children. This section describes different ways of adding them to the composition.

All the below mentioned composables should be added to the `View` of the parent node.


## Children

Renders all visible children of a routing source. This is the simplest and most common case.

```kotlin
@Composable
override fun View(modifier: Modifier) {
    Children(
        modifier = Modifier, // optional
        routingSource = TODO(),
        transitionHandler = TODO() // optional
    )
}
```

## Child

Renders a single child associated to a `RoutingElement`. Useful if you want to define different child placements in the layout individually. 

```kotlin
@Composable
override fun View(modifier: Modifier) {
    Child(
        routingElement = element,
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
    // TODO grab all visible children from the routing source manually
    val children by routingSource.visibleChildrenAsState()
    GridExample(children)
}

@Composable
private fun GridExample(elements: List<RoutingElement<Routing, out Any?>>) {
    LazyVerticalGrid(
        columns = Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(elements) { element ->
            // TODO use Child composable to render them individually inside the list / grid
            Child(routingElement = element)
        }
    }
}
```


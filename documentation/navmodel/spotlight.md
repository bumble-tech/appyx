# Spotlight

<img src="https://i.imgur.com/xtPRfij.gif" width="200">

Implements a mechanism analogous to a view pager; has a single active element ("it's in the spotlight", hence the name), but unlike the back stack, it does not remove other elements.

It's great for flows or tabbed containers.

## States

```kotlin
@Parcelize
enum class State : Parcelable {
    INACTIVE_BEFORE, ACTIVE, INACTIVE_AFTER;
}
```

## Constructing spotlight

Requires defining items and an active index.

```kotlin
class Spotlight<NavTarget : Parcelable>(
    items: List<NavTarget>,
    initialActiveIndex: Int = 0,
    savedStateMap: SavedStateMap?,
    // Optional parameters are omitted
)
```

## Default on screen resolution

As a default, only the active element is considered on screen.

```kotlin
object SpotlightOnScreenResolver : OnScreenStateResolver<Spotlight.State> {
    override fun isOnScreen(state: Spotlight.State): Boolean =
        when (state) {
            Spotlight.State.INACTIVE_BEFORE,
            Spotlight.State.INACTIVE_AFTER -> false
            Spotlight.State.ACTIVE -> true
        }
}
```

## Default transition handlers

#### SpotlightFader

`rememberSpotlightFader()`

Adds simple cross-fading transitions


#### SpotlightSlider

`rememberSpotlightSlider()`

Adds horizontal sliding transitions so that the `ACTIVE` element is in the center; other states are animated from / to the left or the right edge of the screen, depending on the order of them in the `items` property.


## Operations

#### Activate

`spotlight.activate(navTarget)`

Transitions the element to `ACTIVE`. Transitions other elements to `INACTIVE_BEFORE` or `INACTIVE_AFTER` depending on their relative position to the activated element.


#### Next

`spotlight.next()`

Transitions the currently active element to `INACTIVE_BEFORE`. 
Transitions the element after the currently active one to `ACTIVE`.


#### Previous

`spotlight.previous()`

Transitions the currently active element to `INACTIVE_AFTER`.
Transitions the element before the currently active one to `ACTIVE`.


#### Update elements

`spotlight.updateElements(items, activeIndex)`

Replaces elements held by the spotlight instance with a new list. Transitions new elements to `INACTIVE_BEFORE`, `ACTIVE`, or `INACTIVE_AFTER` depending on their position in the provided list relative to `activeIndex`.


## Back press strategy

You can override the default strategy in the constructor. You're not limited to using the provided classes, feel free to implement your own.

```kotlin
class Spotlight<NavTarget : Parcelable>(
    /* ... */
    backPressHandler: BackPressHandlerStrategy<NavTarget, State> = GoToDefault(
        initialActiveIndex
    )
    /* ... */
)
```

#### GoToDefault

The default back press handling strategy. Activates the default index.

#### GoToPrevious

Runs a `Previous` operation.


## Operation strategy

You can override the default strategy in the constructor. You're not limited to using the provided classes, feel free to implement your own.

```kotlin
class Spotlight<NavTarget : Parcelable>(
    /* ... */
    operationStrategy: OperationStrategy<NavTarget, State> = ExecuteImmediately(),    
    /* ... */
)
```

#### ExecuteImmediately
The default strategy. New operations are executed without any questions, regardless of any already running transitions.

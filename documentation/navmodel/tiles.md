# Tiles

<img src="https://i.imgur.com/N8rEPrJ.gif" width="200">

Intended only as an illustration, but it should be easy enough to tailor it to your needs if you find it useful.

## States

```kotlin
enum class TransitionState {
    CREATED, STANDARD, SELECTED, DESTROYED
}
```

## Default on screen resolution

```kotlin
internal object TilesOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.CREATED,
            TransitionState.STANDARD,
            TransitionState.SELECTED -> true
            TransitionState.DESTROYED -> false
        }
}
```

## Default transition handler

Selection translates to scaling.
Destroying makes elements fly off the screen with rotation and downscaling.


## Operations

#### Add

`tiles.add(navTarget)`

Adds a new element to the NavModel immediately transitioning from `CREATED` -> `STANDARD`.


#### Destroy

`tiles.destroy(navTarget)`

Transitions a given element to `DESTROYED`.


#### Select

`tiles.select(navTarget)`

Transitions a given element `STANDARD` -> `SELECTED`.


#### Deselect

`tiles.deselect(navTarget)`

Transitions a given element `SELECTED` -> `STANDARD`.


#### Deselect all

`tiles.deselectAll()`

Transitions all elements `SELECTED` -> `STANDARD`.



#### Remove selected

`tiles.removeSelected()`

Transitions all elements that have `SELECTED` state to `DESTROYED`.

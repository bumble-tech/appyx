# Tiles

<img src="https://i.imgur.com/N8rEPrJ.gif" width="200">

Intended only as an illustration, but it should be easy enough to tailor it to your needs if you find it useful.

## States

```kotlin
enum class State {
    CREATED, STANDARD, SELECTED, DESTROYED
}
```

## Default on screen resolution

```kotlin
internal object TilesOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            State.CREATED,
            State.STANDARD,
            State.SELECTED -> true
            State.DESTROYED -> false
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

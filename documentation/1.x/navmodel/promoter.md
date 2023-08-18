{% include-markdown "../deprecation.md" %}

# Promoter carousel

<img src="https://i.imgur.com/esLXh61.gif" width="200">

Intended only as an illustration.

## Where can I find this NavModel?

The `Promoter` NavModel is not currently published, however you can fork the Appyx repository and try it out yourself!
If you feel that this functionality should be part of the main library, please let us know.

## States

```kotlin
enum class State {
    CREATED, STAGE1, STAGE2, STAGE3, STAGE4, SELECTED, DESTROYED
}
```

## Default on screen resolution

```kotlin
internal object PromoterOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            State.DESTROYED -> false
            else -> true
        }
}

```

## Default transition handler

As elements are promoted to next stages, they're:

- animated on a circular path
- scaled up
- rotated in the selection / discard stages

You can check `PromoterTransitionHandler` for implementation details.


## Operations

#### Add first

`promoter.addFirst(navTarget)`

Adds a new element at the start of the element list with a `CREATED` state.


#### Promote all

`promoter.promoteAll()`

All elements are transitioned to the next state:

- `CREATED` -> `STAGE1`
- `STAGE1` -> `STAGE2`
- `STAGE2` -> `STAGE3`
- `STAGE3` -> `STAGE4`
- `STAGE4` -> `SELECTED`
- `SELECTED` -> `DESTROYED`


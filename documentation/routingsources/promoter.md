# Promoter carousel

Intended only as an illustration.

## States

```kotlin
enum class TransitionState {
    CREATED, STAGE1, STAGE2, STAGE3, STAGE4, SELECTED, DESTROYED
}
```

## Default on screen resolution

```kotlin
internal object PromoterOnScreenResolver : OnScreenStateResolver<TransitionState> {
    override fun isOnScreen(state: TransitionState): Boolean =
        when (state) {
            TransitionState.DESTROYED -> false
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

`promoter.addFirst(routing)`

Adds a new element at the start of the element list with a `CREATED` state.


#### Promote all

`promoter.promoteAll(routing)`

All elements are transitioned to the next state:

`CREATED` -> `STAGE1`
`STAGE1` -> `STAGE2`
`STAGE2` -> `STAGE3`
`STAGE3` -> `STAGE4`
`STAGE4` -> `SELECTED`
`SELECTED` -> `DESTROYED`


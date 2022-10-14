# Cards

<img src="https://cdn-images-1.medium.com/max/1600/1*mEg8Ebem3Hd2knQSA0yI1A.gif" width="200">

Implements a dating-cards-like mechanism.

Intended only as an illustration, but it should be easy enough to tailor it to your needs if you find it useful.

## Where can I find this NavModel?

The `Cards` NavModel is not currently published, however you can try it in `:samples:app`. Launch the sample app and check the `Dating cards NavModel` item to see it in action.



## States

```kotlin
sealed class State {
    data class Queued(val queueNumber: Int) : State()
    object Bottom : State()
    object Top : State()
    object IndicateLike : State()
    object IndicatePass : State()
    object VoteLike : State()
    object VotePass : State()
}
```

## State transitions

<img src="https://cdn-images-1.medium.com/max/1600/1*PLL5ip5-5LLjk3e9IhZIxA.png" width="400" />

## Constructing `Cards`

Requires defining items that will be converted to profile cards. The first one in the list will become a `Top` card, the second one a `Bottom` card, the rest will be `Queued`. 

```kotlin
class Cards<NavTarget : Any>(
    initialItems: List<NavTarget> = listOf(),
) : BaseNavModel<NavTarget, State>(
    screenResolver = CardsOnScreenResolver,
    finalStates = FINAL_STATES,
    savedStateMap = null
) {
    companion object {
        internal val FINAL_STATES = setOf(VoteLike, VotePass)
        internal val TOP_STATES = setOf(Top, IndicateLike, IndicatePass)
    }
}
```

## Default on screen resolution

```kotlin
internal object CardsOnScreenResolver : OnScreenStateResolver<State> {
    override fun isOnScreen(state: State): Boolean =
        when (state) {
            is State.Bottom,
            is State.Top,
            is State.IndicateLike,
            is State.IndicatePass -> true
            is State.Queued,
            is State.VoteLike,
            is State.VotePass -> false
        }
}
```

## Default transition handlers

#### CardsTransitionHandler

`rememberCardsTransitionHandler()`

Adds scale-up, swipe and rotation animations.


## Operations

#### PromoteAll

Internal operation. Automatically invoked whenever a top card is moved to a vote-related state. Causes all other cards to come forward in the queue, become the `Bottom` card, then the `Top` card.


#### IndicateLike, IndicatePass, VoteLike, VotePass

Transitions the `Top` card directly to these states.


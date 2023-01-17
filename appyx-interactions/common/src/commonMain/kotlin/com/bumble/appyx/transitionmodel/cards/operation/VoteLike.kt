package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.transitionmodel.cards.Cards
import com.bumble.appyx.interactions.Parcelize

@Parcelize
class VoteLike<NavTarget : Any> : TopCardOperation<NavTarget>(Cards.State.VoteLike)

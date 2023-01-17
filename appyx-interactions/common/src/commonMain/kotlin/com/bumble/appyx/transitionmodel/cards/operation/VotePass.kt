package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.transitionmodel.cards.Cards
import kotlinx.parcelize.Parcelize

@Parcelize
class VotePass<NavTarget : Any> : TopCardOperation<NavTarget>(Cards.State.VotePass)

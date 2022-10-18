package com.bumble.appyx.navmodel2.cards.operation

import com.bumble.appyx.navmodel2.cards.Cards
import kotlinx.parcelize.Parcelize

@Parcelize
class VotePass<NavTarget : Any> : TopCardOperation<NavTarget>(Cards.State.VotePass)

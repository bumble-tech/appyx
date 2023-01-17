package com.bumble.appyx.transitionmodel.cards.operation

import com.bumble.appyx.interactions.core.navigation2.Operation
import com.bumble.appyx.transitionmodel.cards.Cards

interface CardsOperation<NavTarget> : Operation<NavTarget, Cards.State>

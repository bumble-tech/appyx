package com.bumble.appyx.transitionmodel.promoter.operation

import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.promoter.Promoter

interface PromoterOperation<NavTarget> : Operation<NavTarget, Promoter.State>

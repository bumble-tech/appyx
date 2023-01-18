package com.bumble.appyx.transitionmodel.promoter.operation

import com.bumble.appyx.interactions.core.Operation
import com.bumble.appyx.transitionmodel.promoter.PromoterModel

interface PromoterOperation<NavTarget> : Operation<NavTarget, PromoterModel.State>

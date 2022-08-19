package com.bumble.appyx.navmodel.promoter.navmodel.operation

import com.bumble.appyx.navmodel.promoter.navmodel.Promoter
import com.bumble.appyx.core.navigation.Operation

sealed interface PromoterOperation<T> : Operation<T, Promoter.TransitionState>

package com.bumble.appyx.routingsource.promoter.routingsource.operation

import com.bumble.appyx.routingsource.promoter.routingsource.Promoter
import com.bumble.appyx.core.routing.Operation

sealed interface PromoterOperation<T> : Operation<T, Promoter.TransitionState>

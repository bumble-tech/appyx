package com.bumble.appyx.routingsourcedemos.promoter.routingsource.operation

import com.bumble.appyx.routingsourcedemos.promoter.routingsource.Promoter
import com.bumble.appyx.core.routing.Operation

sealed interface PromoterOperation<T> : Operation<T, Promoter.TransitionState>

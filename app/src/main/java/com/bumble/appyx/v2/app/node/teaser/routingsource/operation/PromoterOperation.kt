package com.bumble.appyx.v2.app.node.teaser.routingsource.operation

import com.bumble.appyx.v2.app.node.teaser.routingsource.Promoter
import com.bumble.appyx.v2.core.routing.Operation

sealed interface PromoterOperation<T> : Operation<T, Promoter.TransitionState>

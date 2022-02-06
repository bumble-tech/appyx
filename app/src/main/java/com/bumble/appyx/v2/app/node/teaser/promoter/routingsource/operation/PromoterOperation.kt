package com.bumble.appyx.v2.app.node.teaser.promoter.routingsource.operation

import com.bumble.appyx.v2.app.node.teaser.promoter.routingsource.Promoter
import com.bumble.appyx.v2.core.routing.Operation

sealed interface PromoterOperation<T> : Operation<T, Promoter.TransitionState>

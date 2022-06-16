package com.bumble.appyx.app.node.teaser.promoter.routingsource.operation

import com.bumble.appyx.app.node.teaser.promoter.routingsource.Promoter
import com.bumble.appyx.core.routing.Operation

sealed interface PromoterOperation<T> : Operation<T, Promoter.TransitionState>

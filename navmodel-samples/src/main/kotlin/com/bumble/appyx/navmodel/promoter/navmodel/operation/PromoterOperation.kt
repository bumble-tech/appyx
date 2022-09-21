package com.bumble.appyx.navmodel.promoter.navmodel.operation

import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter

interface PromoterOperation<T> : Operation<T, Promoter.State>

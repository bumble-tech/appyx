package com.bumble.appyx.navmodel.promoter.navmodel2.operation

import com.bumble.appyx.core.navigation2.Operation
import com.bumble.appyx.navmodel.promoter.navmodel2.Promoter

interface PromoterOperation<NavTarget> : Operation<NavTarget, Promoter.State>

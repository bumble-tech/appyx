package com.bumble.appyx.navmodel.promoter.navmodel.operation

import android.os.Parcelable
import com.bumble.appyx.core.navigation.Operation
import com.bumble.appyx.navmodel.promoter.navmodel.Promoter

interface PromoterOperation<T : Parcelable> : Operation<T, Promoter.State>

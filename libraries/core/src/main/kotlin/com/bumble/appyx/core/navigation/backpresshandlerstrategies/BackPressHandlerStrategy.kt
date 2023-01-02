package com.bumble.appyx.core.navigation.backpresshandlerstrategies

import android.os.Parcelable
import com.bumble.appyx.core.navigation.BaseNavModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BackPressHandlerStrategy<NavTarget : Parcelable, State : Parcelable> {

    fun init(navModel: BaseNavModel<NavTarget, State>, scope: CoroutineScope)

    val canHandleBackPress: StateFlow<Boolean>

    fun onBackPressed()

}

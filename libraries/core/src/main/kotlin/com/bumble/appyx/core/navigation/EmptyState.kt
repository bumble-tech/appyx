package com.bumble.appyx.core.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * There are cases where you may need to have a NavModel without state.
 * As state is [Parcelable] this class can be used to meet the contract.
 */
@Suppress("PARCELABLE_PRIMARY_CONSTRUCTOR_IS_EMPTY")
@Parcelize
class EmptyState internal constructor() : Parcelable {
    companion object {
        val INSTANCE = EmptyState()
    }
}

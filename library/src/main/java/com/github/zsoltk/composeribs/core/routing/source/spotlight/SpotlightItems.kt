package com.github.zsoltk.composeribs.core.routing.source.spotlight

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SpotlightItems<T : Parcelable, K : Parcelable>(
    val items: List<SpotlightItem<T, K>>,
    val initialElementKey: K? = null,
) : Parcelable

@Parcelize
class SpotlightItem<T : Parcelable, K : Parcelable>(
    val element: T,
    val key: K,
) : Parcelable

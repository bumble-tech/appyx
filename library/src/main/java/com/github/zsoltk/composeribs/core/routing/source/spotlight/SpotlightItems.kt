package com.github.zsoltk.composeribs.core.routing.source.spotlight

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SpotlightItems<T : Parcelable, E : Parcelable>(
    val items: List<SpotlightItem<T, E>>,
    val initialElementKey: E? = null,
) : Parcelable

@Parcelize
class SpotlightItem<T : Parcelable, E : Parcelable>(
    val element: T,
    val key: E,
) : Parcelable
package com.bumble.appyx.v2.core.routing.source.spotlight

import android.os.Parcelable
import kotlinx.coroutines.flow.map

fun <T : Parcelable> Spotlight<T, *>.hasNext() =
    elements.map { value -> value.lastIndex != elements.value.currentIndex }

fun <T : Parcelable> Spotlight<T, *>.hasPrevious() =
    elements.map { value -> value.currentIndex != 0 }

fun <T : Parcelable> Spotlight<T, *>.activeIndex() =
    elements.map { value -> value.currentIndex }

fun <T : Parcelable> Spotlight<T, *>.elementsCount() =
    elements.value.size

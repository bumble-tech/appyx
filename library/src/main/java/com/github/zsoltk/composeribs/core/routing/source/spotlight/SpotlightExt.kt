package com.github.zsoltk.composeribs.core.routing.source.spotlight

import android.os.Parcelable
import kotlinx.coroutines.flow.map

fun <T : Parcelable> Spotlight<T, *>.hasNext() =
    elements.map { value -> value.lastIndex != elements.value.currentIndex }

fun <T : Parcelable> Spotlight<T, *>.hasPrevious() =
    elements.map { value -> value.currentIndex != 0 }
package com.bumble.appyx.navmodel.spotlight

import android.os.Parcelable
import kotlinx.coroutines.flow.map

fun <T : Parcelable> Spotlight<T>.hasNext() =
    elements.map { value -> value.lastIndex != elements.value.currentIndex }

fun <T : Parcelable> Spotlight<T>.hasPrevious() =
    elements.map { value -> value.currentIndex != 0 }

fun <T : Parcelable> Spotlight<T>.activeIndex() =
    elements.map { value -> value.currentIndex }

fun <T : Parcelable> Spotlight<T>.current() =
    elements.map { value -> value.current?.key?.navTarget }

fun <T : Parcelable> Spotlight<T>.elementsCount() =
    elements.value.size

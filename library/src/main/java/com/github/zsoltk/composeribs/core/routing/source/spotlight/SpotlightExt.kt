package com.github.zsoltk.composeribs.core.routing.source.spotlight

import android.os.Parcelable

fun <T : Parcelable> Spotlight<T, *>.hasNext() =
    elements.value.lastIndex != elements.value.currentIndex

fun <T : Parcelable> Spotlight<T, *>.hasPrevious() =
    elements.value.currentIndex != 0
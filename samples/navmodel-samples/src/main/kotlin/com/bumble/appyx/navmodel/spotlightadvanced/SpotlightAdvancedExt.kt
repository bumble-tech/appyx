package com.bumble.appyx.navmodel.spotlightadvanced

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T : Parcelable> SpotlightAdvanced<T>.hasNext() =
    elements.map { value -> value.lastIndex != elements.value.currentIndex }

fun <T : Parcelable> SpotlightAdvanced<T>.hasPrevious() =
    elements.map { value -> value.currentIndex != 0 }

fun <T : Parcelable> SpotlightAdvanced<T>.activeIndex() =
    elements.map { value -> value.currentIndex }

fun <T : Parcelable> SpotlightAdvanced<T>.elementsCount() =
    elements.value.size

fun <T : Parcelable> SpotlightAdvanced<T>.isCarousel(): Flow<Boolean> =
    elements.map { it.any { it.targetState is SpotlightAdvanced.State.Carousel } }

package com.bumble.appyx.navmodel.spotlightadvanced

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T : Any> SpotlightAdvanced<T>.hasNext() =
    elements.map { value -> value.lastIndex != elements.value.currentIndex }

fun <T : Any> SpotlightAdvanced<T>.hasPrevious() =
    elements.map { value -> value.currentIndex != 0 }

fun <T : Any> SpotlightAdvanced<T>.activeIndex() =
    elements.map { value -> value.currentIndex }

fun <T : Any> SpotlightAdvanced<T>.elementsCount() =
    elements.value.size

fun <T : Any> SpotlightAdvanced<T>.isCarousel(): Flow<Boolean> =
    elements.map { it.any { it.targetState is SpotlightAdvanced.TransitionState.Carousel } }

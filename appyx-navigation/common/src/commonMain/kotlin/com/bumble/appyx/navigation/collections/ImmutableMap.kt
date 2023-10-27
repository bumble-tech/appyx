package com.bumble.appyx.navigation.collections

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableMap<K, out V>(private val map: Map<K, V>) : Map<K, V> by map

fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> =
    ImmutableMap(this)

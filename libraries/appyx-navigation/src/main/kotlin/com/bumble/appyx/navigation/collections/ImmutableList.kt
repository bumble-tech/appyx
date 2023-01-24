package com.bumble.appyx.navigation.collections

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableList<out E>(private val list: List<E>) : List<E> by list

fun <E> immutableListOf(vararg items: E): ImmutableList<E> =
    ImmutableList(listOf(*items))

fun <E> List<E>.toImmutableList(): ImmutableList<E> =
    ImmutableList(ArrayList(this)) // make a copy as list might be also mutable

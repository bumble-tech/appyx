package com.bumble.appyx.navigation.node.cakes.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Cart {

    private val mutableItems = MutableStateFlow<Map<Cake, Int>>(emptyMap())
    val items: StateFlow<Map<Cake, Int>> = mutableItems

    fun add(cake: Cake) {
        mutableItems.value = mutableItems.value.toMutableMap().apply {
            this[cake] = (this[cake] ?: 0) + 1
        }
    }

    fun clear() {
        mutableItems.value = emptyMap()
    }
}
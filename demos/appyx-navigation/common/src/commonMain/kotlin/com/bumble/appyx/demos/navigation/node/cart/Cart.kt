package com.bumble.appyx.demos.navigation.node.cart

import com.bumble.appyx.demos.navigation.node.cakes.Cake
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class Cart {

    private val mutableItems = MutableStateFlow<Map<Cake, Int>>(emptyMap())
    val items: Flow<Map<Cake, Int>> = mutableItems

    fun add(cake: Cake) {
        mutableItems.update {
            it.toMutableMap().apply {
                this[cake] = (this[cake] ?: 0) + 1
            }
        }
    }

    fun minusOrDelete(cake: Cake) {
        mutableItems.update {
            it.toMutableMap().apply {
                val currentQuantity = this[cake]
                if (currentQuantity != null && currentQuantity > 1) {
                    this[cake] = currentQuantity - 1
                } else {
                    this.remove(cake)
                }
            }
        }
    }

    fun delete(cake: Cake) {
        mutableItems.update {
            it.toMutableMap().apply {
                this.remove(cake)
            }
        }
    }

    fun clear() {
        mutableItems.value = emptyMap()
    }
}

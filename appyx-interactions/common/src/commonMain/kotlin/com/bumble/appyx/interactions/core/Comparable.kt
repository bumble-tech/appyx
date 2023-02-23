package com.bumble.appyx.interactions.core

interface Comparable<in T> {
    
    fun isEqualTo(other: T): Boolean
}

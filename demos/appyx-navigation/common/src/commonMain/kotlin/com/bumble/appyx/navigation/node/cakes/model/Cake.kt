package com.bumble.appyx.navigation.node.cakes.model

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
data class Cake(
    val name: String
) : Parcelable

val cakes = listOf(
    Cake("Cupcake"),
    Cake("Red velvet cake"),
    Cake("Chocolate cake"),
    Cake("Apple pie"),
    Cake("Donut"),
    Cake("Strawberry cake"),
    Cake("Blueberry muffin"),
    Cake("Cheesecake"),
    Cake("Macaroon"),
)

package com.bumble.appyx.navigation.node.cakes.model

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

@Parcelize
data class Cake(
    val name: String,
    val image: String,
    val backgroundColor: Long,
    val unsplashUrl: String,
    val author: String,
) : Parcelable

val cakes = listOf(
    Cake(
        name = "Macaron",
        image = "macaron.png",
        backgroundColor = 0xFF00bcd4,
        unsplashUrl = "https://unsplash.com/photos/i5BY6W2ttts",
        author = "Diana Polekhina"
    ),
    Cake(
        name = "Mint cupcake",
        image = "mint-cupcake.png",
        backgroundColor = 0xFF8BC34A,
        unsplashUrl = "https://unsplash.com/photos/Ao09kk2ovB0",
        author = "Meritt Thomas"
    ),
    Cake(
        name = "Fruit tart",
        image = "tart.png",
        backgroundColor = 0xFFFFC107,
        unsplashUrl = "https://unsplash.com/photos/OrkEasJeY74",
        author = "Kim Daniels"
    ),
    Cake(
        name = "Cheesecake",
        image = "cheesecake.png",
        backgroundColor = 0xFF607D8B,
        unsplashUrl = "https://unsplash.com/photos/TB0Ao4CQRqc",
        author = "Waranya Mooldee"
    ),
    Cake(
        name = "Red velvet cupcake",
        image = "red-cupcake.png",
        backgroundColor = 0xFFF05D5E,
        unsplashUrl = "https://unsplash.com/photos/MJPr6nOdppw",
        author = "luisana zerpa"
    ),
    Cake(
        name = "Donuts",
        image = "donuts.png",
        backgroundColor = 0xFFF5C5C8,
        unsplashUrl = "https://unsplash.com/photos/PFzy4N0_R3M",
        author = "Elena Koycheva"
    ),
)

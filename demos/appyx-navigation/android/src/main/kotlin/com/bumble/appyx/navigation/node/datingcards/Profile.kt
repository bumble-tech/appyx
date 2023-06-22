package com.bumble.appyx.navigation.node.datingcards

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.bumble.appyx.demos.appyxnavigation.R
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
@Suppress("MagicNumber")
@Immutable
data class Profile(
    val name: String,
    val age: Int = Random.nextInt(20, 35),
    val city: String = "London",
    val uni: String = "Oxford University",
    @DrawableRes val drawableRes: Int = allProfiles.random().drawableRes
) : Parcelable {

    companion object {
        val profile002 = Profile(
            name = "Victoria",
            drawableRes = R.drawable.halloween3
        )

        val profile1001 = Profile(
            name = "Brittany",
            drawableRes = R.drawable.halloween4
        )

        val profile1003 = Profile(
            name = "Jill",
            drawableRes = R.drawable.halloween6
        )

        val profile2001 = Profile(
            name = "Matt",
            drawableRes = R.drawable.halloween7
        )

        val profile2002 = Profile(
            name = "Ryan",
            drawableRes = R.drawable.halloween8
        )

        val profile2003 = Profile(
            name = "Chris",
            drawableRes = R.drawable.halloween9
        )

        val profile2004 = Profile(
            name = "Daniel",
            drawableRes = R.drawable.halloween10
        )

        val profile3001 = Profile(
            name = "Imogen",
            drawableRes = R.drawable.halloween11
        )

        val profile3002 = Profile(
            name = "Heather",
            drawableRes = R.drawable.halloween12
        )

        val profile3003 = Profile(
            name = "Maria",
            drawableRes = R.drawable.halloween13
        )

        val profile3004 = Profile(
            name = "Sophia",
            drawableRes = R.drawable.halloween14
        )

        val profile3005 = Profile(
            name = "Sophia",
            drawableRes = R.drawable.halloween15
        )

        val allProfiles = listOf(
            profile002,
            profile1001,
            profile1003,
            profile2001,
            profile2002,
            profile2003,
            profile2004,
            profile3001,
            profile3002,
            profile3003,
            profile3004,
            profile3005,
        )
    }
}

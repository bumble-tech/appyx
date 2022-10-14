package com.bumble.appyx.samples.common.profile

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.bumble.appyx.samples.common.R
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
@Suppress("MagicNumber")
data class Profile(
    val name: String,
    val age: Int = Random.nextInt(20, 35),
    val city: String = "London",
    val uni: String = "Oxford University",
    @DrawableRes val drawable: Int = allProfiles.random().drawable
) : Parcelable {

    companion object {
        val profile002 = Profile(
            name = "Victoria",
            drawable = R.drawable.halloween3
        )

        val profile1001 = Profile(
            name = "Brittany",
            drawable = R.drawable.halloween4
        )

        val profile1003 = Profile(
            name = "Jill",
            drawable = R.drawable.halloween6
        )

        val profile2001 = Profile(
            name = "Matt",
            drawable = R.drawable.halloween7
        )

        val profile2002 = Profile(
            name = "Ryan",
            drawable = R.drawable.halloween8
        )

        val profile2003 = Profile(
            name = "Chris",
            drawable = R.drawable.halloween9
        )

        val profile2004 = Profile(
            name = "Daniel",
            drawable = R.drawable.halloween10
        )

        val profile3001 = Profile(
            name = "Imogen",
            drawable = R.drawable.halloween11
        )

        val profile3002 = Profile(
            name = "Heather",
            drawable = R.drawable.halloween12
        )

        val profile3003 = Profile(
            name = "Maria",
            drawable = R.drawable.halloween13
        )

        val profile3004 = Profile(
            name = "Sophia",
            drawable = R.drawable.halloween14
        )

        val profile3005 = Profile(
            name = "Sophia",
            drawable = R.drawable.halloween15
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


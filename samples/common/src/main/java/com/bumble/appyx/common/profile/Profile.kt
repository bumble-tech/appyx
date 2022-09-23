package com.bumble.appyx.common.profile

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.bumble.appyx.R
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class Profile(
    val name: String,
    val age: Int = Random.nextInt(20, 35),
    val city: String = "London",
    val uni: String = "Oxford University",
    @DrawableRes val drawable: Int = drawables.random()
) : Parcelable {

    companion object {
        val profile001 = Profile(
            name = "Eve",
            drawable = R.drawable.img_8607
        )

        val profile002 = Profile(
            name = "Victoria",
            drawable = R.drawable.img_9113
        )

        val profile1001 = Profile(
            name = "Brittany",
            drawable = R.drawable.brittany8
        )

        val profile1002 = Profile(
            name = "Zoe",
            drawable = R.drawable.brittany41
        )

        val profile1003 = Profile(
            name = "Jill",
            drawable = R.drawable.brittany60
        )

        val profile2001 = Profile(
            name = "Matt",
            drawable = R.drawable.matt12
        )

        val profile2002 = Profile(
            name = "Ryan",
            drawable = R.drawable.matt24
        )

        val profile2003 = Profile(
            name = "Chris",
            drawable = R.drawable.matt60
        )

        val profile2004 = Profile(
            name = "Daniel",
            drawable = R.drawable.matt88
        )

        val profile3001 = Profile(
            name = "Imogen",
            drawable = R.drawable.imogen15
        )

        val profile3002 = Profile(
            name = "Heather",
            drawable = R.drawable.imogen57
        )

        val profile3003 = Profile(
            name = "Maria",
            drawable = R.drawable.imogen60
        )

        val profile3004 = Profile(
            name = "Sophia",
            drawable = R.drawable.imogen75
        )

        val allProfiles = listOf(
            profile001,
            profile002,
            profile1001,
            profile1002,
            profile1003,
            profile2001,
            profile2002,
            profile2003,
            profile2004,
            profile3001,
            profile3002,
            profile3003,
            profile3004,
        )
    }
}

private val cities = listOf(
    "London"
)

private val drawables: List<Int> = listOf(
    R.drawable.img_8607,
    R.drawable.img_9113,
    R.drawable.brittany8,
    R.drawable.brittany41,
    R.drawable.brittany60,
    R.drawable.matt12,
    R.drawable.matt24,
    R.drawable.matt60,
    R.drawable.matt88,
    R.drawable.imogen15,
    R.drawable.imogen57,
    R.drawable.imogen60,
    R.drawable.imogen75
)
package com.bumble.appyx.demos.navigation.node.profile

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

/**
 * A very, very simple user class.
 */
@Parcelize
data class User(
    val name: String
) : Parcelable {

    companion object {
        val Dummy: User = User("User")
    }
}

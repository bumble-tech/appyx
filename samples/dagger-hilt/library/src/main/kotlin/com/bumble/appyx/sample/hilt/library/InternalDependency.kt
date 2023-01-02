package com.bumble.appyx.sample.hilt.library

import android.app.Activity

/**
 * This class exists to ensure that we can construct the factory with internal dependencies.
 */
internal class InternalDependency(private val activity: Activity) {
    fun getActivityName(): String = activity::class.java.simpleName
}

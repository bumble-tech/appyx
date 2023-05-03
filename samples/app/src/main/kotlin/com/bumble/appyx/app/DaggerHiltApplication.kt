package com.bumble.appyx.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * This is only required if using the dagger-hilt library
 */
@HiltAndroidApp
internal class DaggerHiltApplication : Application()

package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.platform.DefaultPlatformLifecycleObserver
import com.bumble.appyx.utils.multiplatform.Logger

internal object LifecycleLogger : DefaultPlatformLifecycleObserver {

    private const val LOG_TAG = "Lifecycle"
    private val LOGGER = Logger()

    override fun onCreate() {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onCreate")
    }

    override fun onStart() {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onStart")
    }

    override fun onResume() {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onResume")
    }

    override fun onPause() {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onPause")
    }

    override fun onStop() {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onStop")
    }

    override fun onDestroy() {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onDestroy")
    }

}

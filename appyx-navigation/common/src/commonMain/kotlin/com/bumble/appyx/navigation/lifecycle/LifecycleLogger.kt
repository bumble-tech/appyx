package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.platform.DefaultLifecycleObserver
import com.bumble.appyx.navigation.platform.LifecycleOwner
import com.bumble.appyx.utils.multiplatform.Logger

internal object LifecycleLogger : DefaultLifecycleObserver {

    private const val LOG_TAG = "Lifecycle"
    private val LOGGER = Logger()

    override fun onCreate(owner: LifecycleOwner) {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        LOGGER.d(LOG_TAG, "${owner::class.simpleName}@${owner.hashCode()} onDestroy")
    }

}

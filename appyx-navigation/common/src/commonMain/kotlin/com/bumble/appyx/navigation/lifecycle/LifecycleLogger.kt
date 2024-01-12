package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.node.AbstractNode
import com.bumble.appyx.utils.multiplatform.AppyxLogger

internal class LifecycleLogger(private val node: AbstractNode) : DefaultPlatformLifecycleObserver {


    override fun onCreate() {
        AppyxLogger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onCreate")
    }

    override fun onStart() {
        AppyxLogger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onStart")
    }

    override fun onResume() {
        AppyxLogger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onResume")
    }

    override fun onPause() {
        AppyxLogger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onPause")
    }

    override fun onStop() {
        AppyxLogger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onStop")
    }

    override fun onDestroy() {
        AppyxLogger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onDestroy")
    }

    companion object {
        private const val LOG_TAG = "Lifecycle"
    }
}

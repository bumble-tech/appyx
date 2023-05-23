package com.bumble.appyx.navigation.lifecycle

import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.utils.multiplatform.Logger

internal class LifecycleLogger(private val node: Node) : DefaultPlatformLifecycleObserver {

    private val logger = Logger()

    override fun onCreate() {
        logger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onCreate")
    }

    override fun onStart() {
        logger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onStart")
    }

    override fun onResume() {
        logger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onResume")
    }

    override fun onPause() {
        logger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onPause")
    }

    override fun onStop() {
        logger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onStop")
    }

    override fun onDestroy() {
        logger.d(LOG_TAG, "${node::class.simpleName}@${node.hashCode()} onDestroy")
    }

    companion object {
        private const val LOG_TAG = "Lifecycle"
    }
}

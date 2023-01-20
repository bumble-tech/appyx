package com.bumble.appyx.navigation.lifecycle

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal object LifecycleLogger : DefaultLifecycleObserver {

    private const val LOG_TAG = "Lifecycle"

    override fun onCreate(owner: LifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(LOG_TAG, "${owner.javaClass.simpleName}@${owner.hashCode()} onDestroy")
    }

}

package com.bumble.appyx.utils.interop.coroutines.plugin

import com.bumble.appyx.interactions.core.plugin.Plugin
import com.bumble.appyx.navigation.plugin.Destroyable
import kotlinx.coroutines.Job

private class DisposeOnDestroy(private val jobs: List<Job>) : Destroyable {
    override fun destroy() {
        jobs.forEach { it.cancel() }
    }
}

fun disposeOnDestroyPlugin(vararg jobs: Job): Plugin = DisposeOnDestroy(jobs.toList())

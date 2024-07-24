package com.bumble.appyx.utils.interop.coroutines.plugin

import com.bumble.appyx.navigation.plugin.Destroyable
import kotlinx.coroutines.Job
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.Test

internal class CoroutinesDisposeOnDestroyTest {
    @Test
    fun `WHEN dispose on destroy plugin created THEN verify is destroyable type`() {
        assertIs<Destroyable>(disposeOnDestroyPlugin())
    }

    @Test
    fun `GIVEN dispose on destroy plugin created with job WHEN destroy THEN job is cancelled`() {
        val job = Job()
        val disposeOnDestroyPlugin = disposeOnDestroyPlugin(job)

        (disposeOnDestroyPlugin as Destroyable).destroy()

        assertTrue(job.isCancelled)
    }

    @Test
    fun `GIVEN dispose on destroy plugin created with multiple jobs WHEN destroy THEN all jobs are cancelled`() {
        val job1 = Job()
        val job2 = Job()
        val disposeOnDestroyPlugin = disposeOnDestroyPlugin(job1, job2)

        (disposeOnDestroyPlugin as Destroyable).destroy()

        assertTrue(job1.isCancelled)
        assertTrue(job2.isCancelled)
    }

    @Test
    fun `WHEN dispose on destroy plugin created with job THEN job is not cancelled`() {
        val job = Job()
        disposeOnDestroyPlugin(job)

        assertFalse(job.isCancelled)
    }
}

package com.bumble.appyx.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssembleFlipBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun test() {
        benchmarkRule.measureRepeated(
            packageName = "com.bumble.appyx.benchmark.app",
            metrics = listOf(
                StartupTimingMetric(),
                FrameTimingMetric()
            ),
            compilationMode = CompilationMode.DEFAULT,
            iterations = 3
        ) {
            pressHome()
            startActivityAndWait()

            device.findObject(By.text("Assemble")).click()

            device.waitUntilIdle()

            device.findObject(By.text("Flip")).click()

            device.waitUntilIdle()

            device.findObject(By.text("Carousel")).click()

            device.waitUntilIdle()

            device.findObject(By.text("Scatter")).click()

            device.waitUntilIdle()
        }
    }

    private fun UiDevice.waitUntilIdle() {
        wait(Until.findObject(By.text("Idle")), 15000)
    }

}

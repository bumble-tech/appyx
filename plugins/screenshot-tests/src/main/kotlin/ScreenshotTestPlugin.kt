import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.register

class ScreenshotTestPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.configure(CommonExtension::class.java) {
            defaultConfig {
                testInstrumentationRunnerArguments["useTestStorageService"] = "true"
            }
            testOptions.managedDevices.devices.register<ManagedVirtualDevice>(DEVICE_NAME) {
                device = "Pixel"
                @Suppress("MagicNumber")
                apiLevel = 30
                systemImageSource = "aosp"
            }
        }

        target.dependencies.add(
            "androidTestUtil",
            target.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
                .findLibrary("androidx-test-utils").get()
        )

        target.tasks.register(TASK_RECORD, RecordScreenshotsTask::class.java) {
            dependsOn(project.tasks.named("${DEVICE_NAME}Check"))
            testResultsFolder.set(
                project.file("build/outputs/managed_device_android_test_additional_output/$DEVICE_NAME")
            )
        }

        target.tasks.register(TASK_COMPARE, CompareScreenshotsTask::class.java) {
            dependsOn(project.tasks.named("${DEVICE_NAME}Check"))
            testResultsFolder.set(
                project.file("build/outputs/managed_device_android_test_additional_output/$DEVICE_NAME")
            )
        }

    }

    companion object {
        const val DEVICE_NAME = "screenshotTestsDevice"
        const val GROUP = "screenshot-tests"
        const val TASK_COMPARE = "screenshotTestsCompareBaseline"
        const val TASK_RECORD = "screenshotTestsRecordBaseline"
        const val RECORDED_SCREENSHOTS_FOLDER = "screenshots"
    }

}

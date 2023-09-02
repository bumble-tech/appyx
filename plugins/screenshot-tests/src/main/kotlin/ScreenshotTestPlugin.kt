import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension

class ScreenshotTestPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.dependencies.add(
            "androidTestUtil",
            target.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
                .findLibrary("androidx-test-utils").get()
        )

        target.tasks.register(TASK_RECORD, RecordScreenshotsTask::class.java) {
            dependsOn(project.tasks.named("${DEVICE_NAME}DebugAndroidTest"))
            testResultsFolder.set(
                project.file("build/outputs/managed_device_android_test_additional_output/debug/$DEVICE_NAME")
            )
        }

        target.tasks.register(TASK_COMPARE, CompareScreenshotsTask::class.java) {
            dependsOn(project.tasks.named("${DEVICE_NAME}DebugAndroidTest"))
            testResultsFolder.set(
                project.file("build/outputs/managed_device_android_test_additional_output/debug/$DEVICE_NAME")
            )
        }

    }

    companion object {
        const val DEVICE_NAME = "uiTestsDevice"
        const val GROUP = "screenshot-tests"
        const val TASK_COMPARE = "screenshotTestsCompareBaseline"
        const val TASK_RECORD = "screenshotTestsRecordBaseline"
        const val RECORDED_SCREENSHOTS_FOLDER = "screenshots"
    }

}

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

open class RecordScreenshotsTask : DefaultTask() {

    @get:InputDirectory
    val testResultsFolder: DirectoryProperty =
        project.objects.directoryProperty()

    @get:OutputDirectory
    val baselineFolder: Directory =
        project.layout.projectDirectory.dir(ScreenshotTestPlugin.RECORDED_SCREENSHOTS_FOLDER)

    init {
        group = ScreenshotTestPlugin.GROUP
        description = "Runs tests on the specific emulator and saves baseline"
    }

    @TaskAction
    fun run() {
        val outputFolder = baselineFolder.asFile
        outputFolder.deleteRecursively()
        outputFolder.mkdirs()
        testResultsFolder.asFile.get().copyRecursively(outputFolder)
    }

}

import org.gradle.api.GradleException
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory

open class CompareScreenshotsTask : Exec() {

    @get:InputDirectory
    val testResultsFolder: DirectoryProperty =
        project.objects.directoryProperty()

    @get:InputDirectory
    val baselineFolder: Directory =
        project.layout.projectDirectory.dir(ScreenshotTestPlugin.RECORDED_SCREENSHOTS_FOLDER)

    @get:OutputDirectory
    val reportDirectory: Provider<Directory> =
        project.layout.buildDirectory.dir("reports").map { it.dir("screenshotTests") }

    init {
        group = ScreenshotTestPlugin.GROUP
        description = "Runs tests on the specific emulator and compare with baseline"
    }

    override fun exec() {
        // Have to copy all screenshots to a separate working folder to make vrt-runner work
        val workingFolder = project.layout.buildDirectory.dir("screenshots").get()
        workingFolder.cleanUp()

        val baseline = workingFolder.dir("baseline")
        val test = workingFolder.dir("test")

        testResultsFolder.get().asFile.copyRecursively(test.asFile)
        baselineFolder.asFile.copyRecursively(baseline.asFile)

        commandLine(
            "npx",
            // auto install
            "--yes",
            // https://www.npmjs.com/package/@magiclab/vrt-runner
            "@magiclab/vrt-runner",
            "--cwd",
            workingFolder.asFile.absoluteFile.path,
            "--output",
            reportDirectory.get().asFile.absoluteFile.path,
        )

        super.exec()

        val content = reportDirectory.get().file("report.json").asFile.readText()
        if (!content.contains("\"failed\":[]")) {
            val indexHtml = reportDirectory.get().file("index.html").asFile.absolutePath
            throw GradleException(
                "Has failed screenshots, check logs and results $indexHtml\n" +
                        "Use ${ScreenshotTestPlugin.TASK_RECORD} to override recorded screenshots."
            )
        }
    }

    private fun Directory.cleanUp() {
        asFile.run {
            deleteRecursively()
            mkdirs()
        }
    }

}

import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory

open class CompareTask : Exec() {

    @get:InputDirectory
    val testResults: DirectoryProperty = project.objects.directoryProperty()

    @get:InputDirectory
    val recordedResults = project.layout.projectDirectory.dir("screenshots")

    @get:OutputDirectory
    val report = project.layout.buildDirectory.dir("reports").map { it.dir("screenshots") }

    override fun exec() {
        val root = project.layout.buildDirectory.dir("screenshots").get()
        val baseline = root.dir("baseline")
        val test = root.dir("test")

        test.asFile.run {
            deleteRecursively()
            mkdirs()
        }
        testResults.get().asFile.copyRecursively(test.asFile)

        baseline.asFile.run {
            deleteRecursively()
            mkdirs()
        }
        recordedResults.asFile.copyRecursively(baseline.asFile)

        commandLine(
            "npx",
            "@magiclab/vrt-runner",
            "--cwd",
            root.asFile.absoluteFile.path,
            "--output",
            report.get().asFile.absoluteFile.path,
        )

        super.exec()

        val content = report.get().file("report.json").asFile.readText()
        if (!content.contains("\"failed\":[]")) {
            throw GradleException("Has failed screenshots, check logs")
        }
    }

}
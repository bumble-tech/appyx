import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.support.appendReproducibleNewLine
import java.io.ByteArrayOutputStream
import java.io.File

abstract class ReleaseDependenciesDiffFilesTask : DefaultTask() {

    @get:Input
    @set:Option(
        option = "baselineDependenciesDirectoryName",
        description = "Name of the baseline dependencies directory"
    )
    var baselineDependenciesDirectoryName: String? = null

    @get:Input
    @set:Option(
        option = "dependenciesDirectoryName",
        description = "Name of the dependencies directory"
    )
    var dependenciesDirectoryName: String? = null

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun compile() {
        val diffResults = getDiffResults(
            baselineDependencyFiles = getDirectoryFiles(
                checkNotNull(baselineDependenciesDirectoryName) {
                    "baselineDependencyFiles was not supplied"
                }
            ),
            dependencyFiles = getDirectoryFiles(
                checkNotNull(dependenciesDirectoryName) { "directoryName was not supplied" }
            )
        )

        outputFile.get().asFile.writeText(
            buildString {
                appendLine("Dependency diff")
                appendLine("```diff")
                if (diffResults.isNotEmpty()) {
                    diffResults.onEach(::appendLine)
                } else {
                    appendLine("No changes")
                }
                appendLine("```")
            }
        )
    }

    private fun getDirectoryFiles(directoryName: String): Array<File> {
        val folderFiles = project
            .rootProject
            .file("build/release-dependencies-diff/$directoryName")
            .listFiles()
        return requireNotNull(folderFiles) { "A null was returned for $directoryName files" }
    }

    @Suppress("ForbiddenComment")
    private fun getDiffResults(
        baselineDependencyFiles: Array<File>,
        dependencyFiles: Array<File>
    ): List<String> =
        //  TODO: Handle cases where modules are added, renamed or removed
        baselineDependencyFiles
            .mapNotNull { baselineDependencyFile: File ->
                val dependencyFile =
                    dependencyFiles.single { it.name == baselineDependencyFile.name }

                val resultLines = executeDependencyTreeDiff(baselineDependencyFile, dependencyFile)
                    .toString("UTF-8")
                    .lineSequence()
                    .filter { it.isNotEmpty() }
                    .toList()

                if (resultLines.isNotEmpty()) {
                    createDiffResult(
                        projectName = baselineDependencyFile.name.replace("_", ":"),
                        resultLines = resultLines
                    )
                } else {
                    null
                }
            }

    private fun executeDependencyTreeDiff(file1: File, file2: File): ByteArrayOutputStream =
        ByteArrayOutputStream().also { outputStream ->
            project.javaexec {
                classpath = project.files(project.rootProject.file("dependency-tree-diff.jar"))
                args(file1, file2)
                standardOutput = outputStream
            }
        }

    private fun createDiffResult(projectName: String, resultLines: List<String>): String =
        buildString {
            appendLine("=========================================")
            appendLine(projectName)
            appendLine("=========================================")
            resultLines.onEach(::appendLine)
            appendReproducibleNewLine()
        }
}

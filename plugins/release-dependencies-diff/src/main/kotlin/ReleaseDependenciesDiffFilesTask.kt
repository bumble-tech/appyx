import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.support.appendReproducibleNewLine
import java.io.ByteArrayOutputStream
import java.io.File

abstract class ReleaseDependenciesDiffFilesTask : DefaultTask() {
    @TaskAction
    fun compile() {
        val diffResults = getDiffResults(
            baselineDependencyFiles = getFolderFiles("baselineFolderName"),
            dependencyFiles = getFolderFiles("folderName")
        )
        println("Dependency diff")
        println("```diff")
        if (diffResults.isNotEmpty()) {
            diffResults.onEach(::println)
        } else {
            println("No changes")
        }
        println("```")
    }

    private fun getFolderFiles(folderNameParam: String): Array<File> {
        if (!project.hasProperty(folderNameParam)) {
            throw IllegalArgumentException("$folderNameParam argument must be set")
        }
        val folderFiles = project
            .rootProject
            .file("build/release-dependencies-diff/${project.property(folderNameParam)}")
            .listFiles()
        return requireNotNull(folderFiles) { "A null was returned for $folderNameParam files" }
    }

    @Suppress("ForbiddenComment")
    private fun getDiffResults(
        baselineDependencyFiles: Array<File>,
        dependencyFiles: Array<File>
    ): List<String> =
        //  TODO: Handle cases where modules are added, renamed or removed
        baselineDependencyFiles
            .mapIndexedNotNull { index: Int, file1: File ->
                val resultLines = executeDependencyTreeDiff(file1, dependencyFiles[index])
                    .toString("UTF-8")
                    .lineSequence()
                    .filter { it.isNotEmpty() }
                    .toList()

                if (resultLines.isNotEmpty()) {
                    createDiffResult(
                        projectName = file1.name.replace("_", ":"),
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

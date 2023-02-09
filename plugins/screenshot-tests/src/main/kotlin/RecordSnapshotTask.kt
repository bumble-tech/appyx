import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

open class RecordSnapshotTask : DefaultTask() {

    @get:InputDirectory
    val testResults: DirectoryProperty = project.objects.directoryProperty()

    @get:OutputDirectory
    val recordedResults = project.layout.projectDirectory.dir("screenshots")

    @TaskAction
    fun run() {
        recordedResults.asFile.deleteRecursively()
        recordedResults.asFile.mkdirs()
        testResults.asFile.get().copyRecursively(recordedResults.asFile)
    }

}
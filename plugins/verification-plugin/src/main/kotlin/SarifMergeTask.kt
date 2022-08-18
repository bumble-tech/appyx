import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

abstract class SarifMergeTask : AbstractExecTask<SarifMergeTask>(SarifMergeTask::class.java) {

    @InputFiles
    @SkipWhenEmpty
    val lintSarifFiles: ConfigurableFileCollection = project.objects.fileCollection()

    @OutputFile
    val mergedSarifFile: RegularFileProperty = project.objects.fileProperty()

    init {
        mergedSarifFile.set(project.layout.buildDirectory.file("lint-merged.sarif"))
        executable = "npx"
    }

    @TaskAction
    override fun exec() {
        val staticArguments =
            listOf(
                "@microsoft/sarif-multitool",
                "merge",
                "--force",
                "--merge-runs",
                "--recurse",
                "--output-file",
                mergedSarifFile.get().asFile.absolutePath,
            )

        args(staticArguments + lintSarifFiles.map { it.absolutePath })

        super.exec()
    }

}

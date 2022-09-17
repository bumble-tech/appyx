import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.diagnostics.DependencyReportTask

class DependencyDiffPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            afterEvaluate {
                registerDependencyTask()
                registerDiffTask()
            }
        }
    }

    private fun Project.registerDependencyTask() {
        val isJavaLibrary = plugins.hasPlugin("java")

        val isAndroidLibrary =
            plugins.hasPlugin("com.android.application") ||
                    plugins.hasPlugin("com.android.library")

        tasks.register("dependencyDiffCreate", DependencyReportTask::class.java) {
            if (!project.hasProperty("folderName")) {
                throw IllegalArgumentException("folderName argument must be set")
            }
            val folderName = project.property("folderName").toString()

            outputFile = project
                .rootProject
                .file("dependency-diff/$folderName/${project.fileName}")

            if (isAndroidLibrary) {
                setConfiguration("releaseRuntimeClasspath")
            } else if (isJavaLibrary) {
                setConfiguration("runtimeClasspath")
            }
        }
    }

    private fun Project.registerDiffTask() {
        tasks.register("dependencyDiffRun", JavaExec::class.java) {
            if (!project.hasProperty("folderName1")) {
                throw IllegalArgumentException("folderName1 argument must be set")
            }
            if (!project.hasProperty("folderName2")) {
                throw IllegalArgumentException("folderName2 argument must be set")
            }
            val folderName1 = project.property("folderName1").toString()
            val folderName2 = project.property("folderName2").toString()

            classpath = files(project.rootProject.file("dependency-tree-diff.jar"))

            val fileName = project.fileName
            args(
                project.rootProject.file("dependency-diff/$folderName1/$fileName"),
                project.rootProject.file("dependency-diff/$folderName2/$fileName")
            )
        }
    }

    private val Project.fileName: String
        get() = project.path.replace(":", "_")
}

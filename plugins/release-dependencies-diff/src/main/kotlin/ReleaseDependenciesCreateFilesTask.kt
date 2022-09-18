import org.gradle.api.Project
import org.gradle.api.tasks.diagnostics.DependencyReportTask
import java.io.File

abstract class ReleaseDependenciesCreateFilesTask : DependencyReportTask() {

    override fun generate(project: Project) {
        val isJavaLibrary = project.plugins.hasPlugin("java")

        val isAndroidLibrary =
            project.plugins.hasPlugin("com.android.application") ||
                    project.plugins.hasPlugin("com.android.library")

        // We only handle android/java libraries. This plugin also picks up 'parent' modules that
        // don't have any code or build.gradle files.
        if (isAndroidLibrary) {
            setConfiguration("releaseRuntimeClasspath")
            super.generate(project)
        } else if (isJavaLibrary) {
            setConfiguration("runtimeClasspath")
            super.generate(project)
        }
    }

    override fun getOutputFile(): File? {
        val folderName = project.property("folderName").toString()

        return project
            .rootProject
            .file("build/release-dependencies-diff/$folderName/${project.fileName}")
    }

    private val Project.fileName: String
        get() = project.path.replace(":", "_")
}

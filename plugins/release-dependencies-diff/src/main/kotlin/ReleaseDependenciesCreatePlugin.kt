import org.gradle.api.Plugin
import org.gradle.api.Project

class ReleaseDependenciesCreatePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            afterEvaluate {
                tasks.register(
                    "releaseDependenciesCreateFiles",
                    ReleaseDependenciesCreateFilesTask::class.java
                )
            }
        }
    }
}

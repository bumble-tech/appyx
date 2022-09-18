import org.gradle.api.Plugin
import org.gradle.api.Project

class ReleaseDependenciesDiffPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            afterEvaluate {
                tasks.register(
                    "releaseDependenciesDiffFiles",
                    ReleaseDependenciesDiffFilesTask::class.java
                ) {
                    outputFile.set(
                        rootProject.file("build/release-dependencies-diff-result.txt")
                    )
                }
            }
        }
    }
}

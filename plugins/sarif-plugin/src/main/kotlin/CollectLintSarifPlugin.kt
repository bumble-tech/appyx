import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin

class CollectLintSarifPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.register(MERGE_TASK_NAME, LintSarifMergeTask::class.java) {
            group = JavaBasePlugin.VERIFICATION_GROUP
        }
    }

    companion object {
        const val MERGE_TASK_NAME: String = "mergeLintSarif"
    }
}

import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ReportLintSarifPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.withId("com.android.library") {
            target.extensions.configure<LibraryExtension> {
                lint.sarifReport = true
            }
            val rootProject = target.rootProject
            rootProject.plugins.withId("appyx-collect-lint-sarif") {
                rootProject.tasks.named(
                    CollectLintSarifPlugin.MERGE_TASK_NAME,
                    LintSarifMergeTask::class.java,
                ) {
                    lintSarifFiles.from(
                        target
                            .tasks
                            .named("lintReportDebug", AndroidLintTask::class.java)
                            .flatMap { it.sarifReportOutputFile }
                    )
                }
            }
        }
    }

}

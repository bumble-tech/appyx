import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class DetektPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("io.gitlab.arturbosch.detekt")
        target.plugins.withId("io.gitlab.arturbosch.detekt") {
            val rootProject = target.rootProject

            target.extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                baseline = target.file("detekt-baseline.xml")
                basePath = rootProject.projectDir.absolutePath

                val localDetektConfig = target.file("detekt.yml")
                val rootDetektConfig = target.rootProject.file("detekt.yml")
                val rootDetektComposeConfig = target.rootProject.file("detekt-compose.yml")
                if (localDetektConfig.exists()) {
                    config.from(localDetektConfig, rootDetektConfig, rootDetektComposeConfig)
                } else {
                    config.from(rootDetektConfig, rootDetektComposeConfig)
                }
            }

            val detektTask = target.tasks.named("detekt", Detekt::class.java)
            detektTask.configure {
                reports.sarif.required.set(true)
            }

            rootProject.plugins.withId("appyx-collect-sarif") {
                rootProject.tasks.named(
                    CollectSarifPlugin.MERGE_DETEKT_TASK_NAME,
                    ReportMergeTask::class.java,
                ) {
                    input.from(detektTask.map { it.sarifReportFile }.orNull)
                    mustRunAfter(detektTask)
                }
            }
        }

        target.dependencies {
            // We add this to all modules (even ones that don't use Compose).
            // Determining if the module uses Compose is very messy, and as this doesn't appear to
            // cause an problem this should be okay for now.
            add(
                "detektPlugins",
                target
                    .rootProject
                    .extensions
                    .getByType<VersionCatalogsExtension>()
                    .named("libs")
                    .findLibrary("detekt-compose")
                    .get()
            )
        }
    }

}

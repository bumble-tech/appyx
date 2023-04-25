import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal class MultiplatformAppyxPublishPlugin : ProjectPlugin() {

    override fun PublicationContainer.createPublications(project: Project) {
        val mppExtension =
            project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val javadocJar by project.tasks.registering(Jar::class) {
            archiveClassifier.set("javadoc")
        }

        val artifactId = project.extensions.getByType(ProjectPluginExtension::class.java).artifactId

        mppExtension.targets.all {
            mavenPublication {
                if (artifactId.isNotEmpty()) {
                    this.artifactId = "$artifactId-$targetName"
                }
                artifact(javadocJar.get())
                configurePublication(project)
            }
        }

    }

    override fun configureDocAndSources(project: Project) = Unit

    override fun getComponentName() = "multiplatform"


}

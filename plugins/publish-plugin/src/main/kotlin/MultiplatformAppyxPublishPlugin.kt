
import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering

internal class MultiplatformAppyxPublishPlugin : ProjectPlugin() {

    override fun PublicationContainer.createPublications(project: Project) = Unit

    override fun PublishingExtension.configurePublications(project: Project) {
        val javadocJar by project.tasks.registering(Jar::class) {
            archiveClassifier.set("javadoc")
        }
        publications.withType(MavenPublication::class.java).configureEach {
            artifact(javadocJar.get())
            configurePublication(project)
        }
    }

    override fun configureDocAndSources(project: Project) = Unit

    override fun getComponentName() = "multiplatform"

}

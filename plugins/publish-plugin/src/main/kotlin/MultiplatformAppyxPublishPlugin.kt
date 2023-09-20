
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar

internal class MultiplatformAppyxPublishPlugin : ProjectPlugin() {

    override fun PublicationContainer.createPublications(project: Project) = Unit

    override fun PublishingExtension.configurePublications(project: Project) {
        publications.withType(MavenPublication::class.java).configureEach {
            val publication = this
            val javadocJar = project.tasks.register("${publication.name}JavadocJar", Jar::class.java) {
                group = JavaBasePlugin.DOCUMENTATION_GROUP
                description = "Assembles ${publication.name} Kotlin docs into a Javadoc jar"
                archiveClassifier.set("javadoc")

                // https://github.com/gradle/gradle/issues/26091
                archiveBaseName.set("${archiveBaseName.get()}-${publication.name}")
            }
            artifact(javadocJar)
            configurePublication(project)
        }
    }

    override fun configureDocAndSources(project: Project) = Unit

    override fun getComponentName() = "multiplatform"

}

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

internal class JavaAppyxPublishPlugin : ProjectPlugin() {
    override fun configureDocAndSources(project: Project) {
        project.configure<JavaPluginExtension> {
            withJavadocJar()
            withSourcesJar()
        }
    }

    override fun PublicationContainer.createPublications(project: Project) {
        create<MavenPublication>("jvm") {
            val artifactId = project.extensions.getByType(ProjectPluginExtension::class.java).artifactId
            if (artifactId.isNotEmpty()) {
                this.artifactId = artifactId
            }
            from(project.components[getComponentName()])
            configurePublication(project)
        }
    }

    override fun getComponentName(): String = "java"
}

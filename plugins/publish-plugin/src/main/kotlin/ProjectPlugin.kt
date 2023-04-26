import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.signing.SigningExtension


open class ProjectPluginExtension {
    var artifactId: String = ""
}

internal abstract class ProjectPlugin : Plugin<Project> {

    companion object {
        val Project.isSnapshotPublication: Boolean
            get() = findProperty("snapshot") == "true"
    }

    override fun apply(project: Project) {
        project.extensions.create("publishingPlugin", ProjectPluginExtension::class.java)
        project.pluginManager.apply {
            apply("maven-publish")
            apply("signing")
        }

        configureDocAndSources(project)

        project.afterEvaluate {
            project.configure<PublishingExtension> {
                configureRepositories(project)
                publications {
                    createPublications(project)
                }
                configurePublications(project)
            }

            project.configure<SigningExtension> {
                configureSigning()
            }

            project.tasks.named("publishAllPublicationsToSonatypeSnapshotRepository") {
                val fail = !project.isSnapshotPublication
                doFirst {
                    if (fail) throw GradleException(
                        "Publishing to snapshot repository with disabled \"snapshot\" flag is permitted"
                    )
                }
            }
        }
    }

    open fun PublishingExtension.configurePublications(project: Project) = Unit

    private fun PublishingExtension.configureRepositories(project: Project) {
        repositories {
            fun addMaven(name: String, url: String) {
                maven {
                    this.name = name
                    this.url = project.uri(url)
                    credentials {
                        username = project.findProperty("sonatype.username").toString()
                        password = project.findProperty("sonatype.password").toString()
                    }
                }
            }
            addMaven("OSSRH", "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            addMaven(
                "SonatypeSnapshot",
                "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            )
        }
    }

    private fun SigningExtension.configureSigning() {
        isRequired = true

        sign(project.extensions.getByType(PublishingExtension::class.java).publications)

        val inMemoryKey = System.getenv("SIGNING_KEY")
        if (inMemoryKey != null) {
            useInMemoryPgpKeys(
                inMemoryKey,
                project.findProperty("signing.password").toString()
            )
        }
    }

    abstract fun PublicationContainer.createPublications(project: Project)

    fun MavenPublication.configurePublication(project: Project) {
        val definedVersion = project.findProperty("library.version")?.toString()
            ?: throw GradleException("'library.version' has not been set")
        groupId = "com.bumble.appyx"
        version = if (project.isSnapshotPublication) {
            "v${definedVersion.split('.').first()}-SNAPSHOT"
        } else {
            definedVersion
        }
        pom {
            name.set("Appyx")
            description.set("Appyx")
            url.set("https://github.com/bumble-tech/appyx")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("bumble")
                    name.set("Bumble")
                    email.set("appyx@team.bumble.com")
                }
            }
            scm {
                connection.set("scm:git:ssh://github.com/bumble-tech/appyx.git")
                developerConnection.set("scm:git:ssh://github.com/bumble-tech/appyx.git")
                url.set("https://github.com/bumble-tech/appyx")
            }
        }
    }

    protected abstract fun configureDocAndSources(project: Project)

    protected abstract fun getComponentName(): String

}

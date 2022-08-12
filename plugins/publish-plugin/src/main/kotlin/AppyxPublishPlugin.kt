import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.plugins.signing.SigningExtension

internal abstract class ProjectPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply {
            apply("maven-publish")
            apply("signing")
        }

        configureDocAndSources(project)

        project.afterEvaluate {
            project.configure<PublishingExtension> {
                configurePublishing(project)
            }

            project.configure<SigningExtension> {
                configureSigning()
            }

            project.tasks.named("publishAppyxReleasePublicationToSonatypeSnapshotRepository") {
                val fail = project.findProperty("snapshot") != "true"
                doFirst {
                    if (fail) throw GradleException("Publishing to snapshot repository with disabled \"snapshot\" flag is permitted")
                }
            }
        }
    }

    protected abstract fun configureDocAndSources(project: Project)

    protected abstract fun getComponentName(): String

    private fun PublishingExtension.configurePublishing(project: Project) {
        publications {
            setupPublications(project)
        }
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
            addMaven("SonatypeSnapshot", "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }

    private fun PublicationContainer.setupPublications(project: Project) {
        create<MavenPublication>("appyxRelease") {
            from(project.components[getComponentName()])
            groupId = "com.bumble.appyx"
            version = if (project.findProperty("snapshot") == "true") {
                "main-SNAPSHOT"
            } else {
                if (!project.hasProperty("library.version")) {
                    throw GradleException("'library.version' has not been set")
                }
                project.property("library.version").toString()
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
    }

    private fun SigningExtension.configureSigning() {
        sign((project.extensions.getByName("publishing") as PublishingExtension).publications["appyxRelease"])
        isRequired = true

        val inMemoryKey = System.getenv("SIGNING_KEY")
        if (inMemoryKey != null) {
            useInMemoryPgpKeys(
                inMemoryKey,
                project.findProperty("signing.password").toString()
            )
        }
    }
}

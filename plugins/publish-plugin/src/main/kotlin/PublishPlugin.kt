import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom

class PublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("com.vanniktech.maven.publish")
        if (project.pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")) {
            project.pluginManager.apply("org.jetbrains.dokka")
        }
        project.extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
            configureBasedOnAppliedPlugins()
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            signAllPublications()
            coordinates(
                artifactId = project.name,
                groupId = "com.bumble.appyx",
                version = project.publishVersion,
            )
            pom { setup() }
        }
    }

    private val Project.isSnapshotPublication: Boolean
        get() = findProperty("snapshot") == "true"

    private val Project.publishVersion: String
        get() {
            val definedVersion = project.findProperty("library.version")?.toString()
                ?: throw GradleException("'library.version' has not been set")
            val version = if (project.isSnapshotPublication) {
                "v${definedVersion.split('.').first()}-SNAPSHOT"
            } else {
                definedVersion
            }
            return version
        }

    private fun MavenPom.setup() {
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

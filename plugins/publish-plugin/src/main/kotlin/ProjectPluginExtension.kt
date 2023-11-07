/**
 * Artifact id for Android and Java publications. When applied to multiplatform project, publication
 * creates wrong artifacts. To configure correct artifactId for Multiplatform module publication please
 * change a project name for the module in settings.gradle file
 */
open class ProjectPluginExtension {
    var artifactId: String = ""
}

import org.gradle.api.Project

internal class MultiplatformAppyxPublishPlugin : ProjectPlugin() {

    companion object {
        const val MULTIPLATFORM = "multiplatform"
    }

    override fun configureDocAndSources(project: Project) = Unit

    override fun getComponentName(): String = MULTIPLATFORM
}

package com.bumble.appyx.dagger.hilt

import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class HiltNodeFactoryProcessorTest {

    @field:TempDir
    lateinit var temporaryFolder: File

    @Test
    fun `generate hilt node factory`() {
        val sampleNodeFactorySource = SourceFile.kotlin(
            "SampleNodeFactory.kt", """
                import com.bumble.appyx.core.integration.NodeFactory
                import com.bumble.appyx.core.modality.BuildContext
                import com.bumble.appyx.core.node.Node
                import com.bumble.appyx.dagger.hilt.HiltNodeFactory
                
                open class SampleNode(buildContext: BuildContext) : Node(buildContext)
                internal class SampleNodeImpl(buildContext: BuildContext) : SampleNode(buildContext)
                
                @HiltNodeFactory(SampleNode::class)
                internal class SampleNodeFactory : NodeFactory<SampleNode> {
                  override fun create(buildContext: BuildContext): SampleNode =
                    SampleNodeImpl(buildContext)
                }
            """.trimIndent()
        )

        val compilation = prepareCompilation(
            temporaryFolder,
            buildContextSourceFile,
            nodeSourceFile,
            nodeFactorySourceFile,
            sampleNodeFactorySource
        )

        verifyFileGenerated(
            compilation = compilation,
            fileName = "SampleNodeFactory_NodeFactoryModule",
            content = """
                import com.bumble.appyx.core.integration.NodeFactory
                import dagger.Binds
                import dagger.Module
                import dagger.hilt.InstallIn
                import dagger.hilt.android.components.ActivityComponent
                import dagger.hilt.codegen.OriginatingElement
                import dagger.multibindings.ClassKey
                import dagger.multibindings.IntoMap

                @Module
                @InstallIn(ActivityComponent::class)
                @OriginatingElement(topLevelClass = SampleNodeFactory::class)
                internal interface SampleNodeFactory_NodeFactoryModule {
                  @Binds
                  @IntoMap
                  @ClassKey(value = SampleNode::class)
                  public fun binds(factory: SampleNodeFactory): NodeFactory<*>
                }
                
            """.trimIndent()
        )
    }
}

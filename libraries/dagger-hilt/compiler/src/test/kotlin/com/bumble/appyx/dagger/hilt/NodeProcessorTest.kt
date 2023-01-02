package com.bumble.appyx.dagger.hilt

import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class NodeProcessorTest {

    @field:TempDir
    lateinit var temporaryFolder: File

    @Test
    fun `generate hilt node without inheritance`() {
        val sampleNodeSource = SourceFile.kotlin(
            "SampleNode.kt", """
                import com.bumble.appyx.core.modality.BuildContext
                import com.bumble.appyx.core.node.Node
                import com.bumble.appyx.dagger.hilt.HiltNode
                
                @HiltNode
                internal class SampleNode(buildContext: BuildContext) : Node(buildContext)
            """.trimIndent()
        )

        val compilation = prepareCompilation(
            temporaryFolder,
            buildContextSourceFile,
            nodeSourceFile,
            sampleNodeSource
        )

        verifyFileGenerated(
            compilation = compilation,
            fileName = "SampleNode_NodeFactoryModule",
            content = """
                import com.bumble.appyx.core.integration.NodeFactory
                import com.bumble.appyx.core.modality.BuildContext
                import dagger.Module
                import dagger.Provides
                import dagger.hilt.InstallIn
                import dagger.hilt.android.components.ActivityComponent
                import dagger.hilt.codegen.OriginatingElement
                import dagger.multibindings.ClassKey
                import dagger.multibindings.IntoMap
                
                @Module
                @InstallIn(ActivityComponent::class)
                @OriginatingElement(topLevelClass = SampleNode::class)
                internal object SampleNode_NodeFactoryModule {
                  @Provides
                  @IntoMap
                  @ClassKey(value = SampleNode::class)
                  internal fun provides(): NodeFactory<*> = SampleNodeNodeFactory()
                
                  internal class SampleNodeNodeFactory : NodeFactory<SampleNode> {
                    public override fun create(buildContext: BuildContext): SampleNode = SampleNode(buildContext)
                  }
                }
                
            """.trimIndent()
        )
    }

    @Test
    fun `generate hilt node with inheritance`() {
        val sampleNodeSource = SourceFile.kotlin(
            "SampleNode.kt", """
                import com.bumble.appyx.core.modality.BuildContext
                import com.bumble.appyx.core.node.Node
                import com.bumble.appyx.dagger.hilt.HiltNode
                
                open class SampleNode(buildContext: BuildContext) : Node(buildContext)
                
                @HiltNode(SampleNode::class)
                internal class SampleNodeImpl(buildContext: BuildContext) : SampleNode(buildContext)
            """.trimIndent()
        )

        val compilation = prepareCompilation(
            temporaryFolder,
            buildContextSourceFile,
            nodeSourceFile,
            sampleNodeSource
        )

        verifyFileGenerated(
            compilation = compilation,
            fileName = "SampleNodeImpl_NodeFactoryModule",
            content = """
                import com.bumble.appyx.core.integration.NodeFactory
                import com.bumble.appyx.core.modality.BuildContext
                import dagger.Module
                import dagger.Provides
                import dagger.hilt.InstallIn
                import dagger.hilt.android.components.ActivityComponent
                import dagger.hilt.codegen.OriginatingElement
                import dagger.multibindings.ClassKey
                import dagger.multibindings.IntoMap
                
                @Module
                @InstallIn(ActivityComponent::class)
                @OriginatingElement(topLevelClass = SampleNodeImpl::class)
                internal object SampleNodeImpl_NodeFactoryModule {
                  @Provides
                  @IntoMap
                  @ClassKey(value = SampleNode::class)
                  internal fun provides(): NodeFactory<*> = SampleNodeNodeFactory()
                
                  internal class SampleNodeNodeFactory : NodeFactory<SampleNode> {
                    public override fun create(buildContext: BuildContext): SampleNode =
                        SampleNodeImpl(buildContext)
                  }
                }
                
            """.trimIndent()
        )
    }
}

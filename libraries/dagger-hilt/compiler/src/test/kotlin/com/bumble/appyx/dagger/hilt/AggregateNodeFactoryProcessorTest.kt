package com.bumble.appyx.dagger.hilt

import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class AggregateNodeFactoryProcessorTest {

    @field:TempDir
    lateinit var temporaryFolder: File

    @Suppress("LongMethod")
    @Test
    fun `generate aggregate hilt node factory`() {
        val sampleNodeFactorySource = SourceFile.kotlin(
            "ExampleParentNode.kt", """
                import com.bumble.appyx.core.integration.NodeFactory
                import com.bumble.appyx.core.modality.BuildContext
                import com.bumble.appyx.core.node.Node
                import com.bumble.appyx.dagger.hilt.HiltAggregateNodeFactory
                
                internal class ExampleNode(buildContext: BuildContext) : Node(buildContext)
                
                interface ExampleCustomNodeFactory {
                    fun build(buildContext: BuildContext, sampleArgument: String): Node
                }
                
                internal class ExampleParentNode {
                  @HiltAggregateNodeFactory
                  interface AggregateNodeFactory {
                    val exampleNodeFactory: NodeFactory<ExampleNode>
                    val exampleCustomNodeFactory: ExampleCustomNodeFactory
                  }
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
            fileName = "AggregateNodeFactory_AggregateNodeFactoryModule",
            content = """
                import com.bumble.appyx.core.integration.NodeFactory
                import com.bumble.appyx.dagger.hilt.NodeFactoryProvider
                import com.bumble.appyx.dagger.hilt.`internal`.AggregateNodeFactory
                import com.bumble.appyx.dagger.hilt.`internal`.AggregateNodeFactoryBuilder
                import com.bumble.appyx.dagger.hilt.`internal`.customNodeFactory
                import com.bumble.appyx.dagger.hilt.`internal`.nodeFactory
                import dagger.Module
                import dagger.Provides
                import dagger.hilt.InstallIn
                import dagger.hilt.android.components.ActivityComponent
                import dagger.hilt.codegen.OriginatingElement
                import dagger.multibindings.ClassKey
                import dagger.multibindings.IntoMap

                @Module
                @InstallIn(ActivityComponent::class)
                @OriginatingElement(topLevelClass = ExampleParentNode::class)
                internal object AggregateNodeFactory_AggregateNodeFactoryModule {
                  @Provides
                  @IntoMap
                  @ClassKey(value = ExampleParentNode.AggregateNodeFactory::class)
                  internal fun provides(): AggregateNodeFactoryBuilder<*> = DaggerAggregateNodeFactoryBuilder()
                
                  internal class DaggerAggregateNodeFactory(
                    public override val nodeFactoryProvider: NodeFactoryProvider,
                  ) : ExampleParentNode.AggregateNodeFactory, AggregateNodeFactory {
                    public override val exampleNodeFactory: NodeFactory<ExampleNode> by nodeFactory<ExampleNode>()
                
                    public override val exampleCustomNodeFactory: ExampleCustomNodeFactory by
                        customNodeFactory<ExampleCustomNodeFactory>()
                  }
                
                  internal class DaggerAggregateNodeFactoryBuilder :
                      AggregateNodeFactoryBuilder<DaggerAggregateNodeFactory> {
                    public override fun build(nodeFactoryProvider: NodeFactoryProvider): DaggerAggregateNodeFactory
                        = DaggerAggregateNodeFactory(nodeFactoryProvider)
                  }
                }
                
            """.trimIndent()
        )
    }
}

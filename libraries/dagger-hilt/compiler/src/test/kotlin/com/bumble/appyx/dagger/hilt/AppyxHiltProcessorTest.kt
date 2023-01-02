package com.bumble.appyx.dagger.hilt

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class AppyxHiltProcessorTest {

    @field:TempDir
    lateinit var temporaryFolder: File

    @Test
    fun `generate hilt appyx node without inheritance`() {
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
    fun `generate hilt appyx node with inheritance`() {
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

    @Test
    fun `generate hilt appyx node factory`() {
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

    @Test
    fun `generate hilt appyx custom node factory without inheritance`() {
        val sampleCustomNodeFactorySource = SourceFile.kotlin(
            "SampleCustomNodeNodeFactory.kt", """
                import com.bumble.appyx.core.modality.BuildContext
                import com.bumble.appyx.core.node.Node
                import com.bumble.appyx.dagger.hilt.CustomNodeFactory
                import com.bumble.appyx.dagger.hilt.HiltCustomNodeFactory
                
                internal class SampleNode(
                    buildContext: BuildContext,
                    sampleArgument: String
                ) : Node(buildContext)
                
                @HiltCustomNodeFactory
                internal class SampleNodeCustomNodeFactory {
                    fun build(
                        buildContext: BuildContext,
                        sampleArgument: String
                    ) = SampleNode(buildContext, sampleArgument)
                }
            """.trimIndent()
        )

        val compilation = prepareCompilation(
            buildContextSourceFile,
            nodeSourceFile,
            sampleCustomNodeFactorySource
        )

        verifyFileGenerated(
            compilation = compilation,
            fileName = "SampleNodeCustomNodeFactory_CustomNodeFactoryModule",
            content = """
                import com.bumble.appyx.dagger.hilt.`internal`.CustomNodeFactoryQualifier
                import dagger.Binds
                import dagger.Module
                import dagger.hilt.InstallIn
                import dagger.hilt.android.components.ActivityComponent
                import dagger.hilt.codegen.OriginatingElement
                import dagger.multibindings.ClassKey
                import dagger.multibindings.IntoMap
                import kotlin.Any
                
                @Module
                @InstallIn(ActivityComponent::class)
                @OriginatingElement(topLevelClass = SampleNodeCustomNodeFactory::class)
                internal interface SampleNodeCustomNodeFactory_CustomNodeFactoryModule {
                  @Binds
                  @IntoMap
                  @ClassKey(value = SampleNodeCustomNodeFactory::class)
                  @CustomNodeFactoryQualifier
                  public fun binds(factory: SampleNodeCustomNodeFactory): Any
                }
                
            """.trimIndent()
        )
    }

    @Test
    fun `generate hilt appyx custom node factory with inheritance`() {
        val sampleCustomNodeFactorySource = SourceFile.kotlin(
            "SampleCustomNodeNodeFactory.kt", """
                import com.bumble.appyx.core.modality.BuildContext
                import com.bumble.appyx.core.node.Node
                import com.bumble.appyx.dagger.hilt.CustomNodeFactory
                import com.bumble.appyx.dagger.hilt.HiltCustomNodeFactory
                
                internal class SampleNode(
                    buildContext: BuildContext,
                    sampleArgument: String
                ) : Node(buildContext)
                
                interface SampleNodeCustomNodeFactory {
                    fun build(buildContext: BuildContext, sampleArgument: String): Node
                }
                
                @HiltCustomNodeFactory(SampleNodeCustomNodeFactory::class)
                internal class SampleNodeCustomNodeFactoryImpl : SampleNodeCustomNodeFactory {
                    override fun build(
                        buildContext: BuildContext,
                        sampleArgument: String
                    ) = SampleNode(buildContext, sampleArgument)
                }
            """.trimIndent()
        )

        val compilation = prepareCompilation(
            buildContextSourceFile,
            nodeSourceFile,
            sampleCustomNodeFactorySource
        )

        verifyFileGenerated(
            compilation = compilation,
            fileName = "SampleNodeCustomNodeFactoryImpl_CustomNodeFactoryModule",
            content = """
                import com.bumble.appyx.dagger.hilt.`internal`.CustomNodeFactoryQualifier
                import dagger.Binds
                import dagger.Module
                import dagger.hilt.InstallIn
                import dagger.hilt.android.components.ActivityComponent
                import dagger.hilt.codegen.OriginatingElement
                import dagger.multibindings.ClassKey
                import dagger.multibindings.IntoMap
                import kotlin.Any
                
                @Module
                @InstallIn(ActivityComponent::class)
                @OriginatingElement(topLevelClass = SampleNodeCustomNodeFactoryImpl::class)
                internal interface SampleNodeCustomNodeFactoryImpl_CustomNodeFactoryModule {
                  @Binds
                  @IntoMap
                  @ClassKey(value = SampleNodeCustomNodeFactory::class)
                  @CustomNodeFactoryQualifier
                  public fun binds(factory: SampleNodeCustomNodeFactoryImpl): Any
                }
                
            """.trimIndent()
        )
    }

    private fun verifyFileGenerated(
        compilation: KotlinCompilation,
        fileName: String,
        content: String,
    ) {
        assertEquals(KotlinCompilation.ExitCode.OK, compilation.compile().exitCode)
        compilation.kspSourcesDir.walkTopDown()
            .single { it.nameWithoutExtension == fileName }
            .let { generatedFile ->
                assertEquals(content, generatedFile.readText())
            }
    }

    private fun prepareCompilation(vararg sourceFiles: SourceFile) =
        KotlinCompilation().apply {
            workingDir = temporaryFolder
            inheritClassPath = true
            symbolProcessorProviders = listOf(AppyxHiltProcessorProvider())
            sources = sourceFiles.asList()
            verbose = false
            kspIncremental = true
        }
}

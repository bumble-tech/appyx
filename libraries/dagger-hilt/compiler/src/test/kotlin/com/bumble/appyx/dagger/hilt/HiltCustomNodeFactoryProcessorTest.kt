package com.bumble.appyx.dagger.hilt

import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class HiltCustomNodeFactoryProcessorTest {

    @field:TempDir
    lateinit var temporaryFolder: File

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
            temporaryFolder,
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
            temporaryFolder,
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
}

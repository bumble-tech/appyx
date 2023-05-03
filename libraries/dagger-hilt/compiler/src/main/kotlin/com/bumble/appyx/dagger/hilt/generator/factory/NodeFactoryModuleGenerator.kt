package com.bumble.appyx.dagger.hilt.generator.factory

import com.bumble.appyx.dagger.hilt.generator.ModuleGenerator
import com.bumble.appyx.dagger.hilt.generator.createDaggerBindsModuleTypeSpec
import com.bumble.appyx.dagger.hilt.generator.nodeFactoryClassName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR

/**
 * Source generator to support Hilt injection of NodeFactory using @HiltNodeFactory.
 *
 * The following inputs:
 *
 * open class SampleNode(buildContext: BuildContext) : Node(buildContext)
 * internal class SampleNodeImpl(buildContext: BuildContext) : SampleNode(buildContext)
 *
 * @HiltNodeFactory(SampleNode::class)
 * internal class SampleNodeFactory @Inject constructor() : NodeFactory<SampleNode> {
 *   override fun create(buildContext: BuildContext): SampleNode =
 *     SampleNodeImpl(buildContext)
 * }
 *
 * We will generate:
 * ```
 * @Module
 * @InstallIn(ActivityComponent::class)
 * @OriginatingElement(topLevelClass = SampleNodeFactory::class)
 * internal interface SampleNodeFactory_NodeFactoryModule {
 *   @Binds
 *   @IntoMap
 *   @ClassKey(value = SampleNode::class)
 *   fun binds(factory: SampleNodeFactory): NodeFactory<*>
 * }
 * ```
 */
internal object NodeFactoryModuleGenerator : ModuleGenerator {

    override fun generateHiltModule(
        codeGenerator: CodeGenerator,
        declaration: KSClassDeclaration
    ) {
        com.bumble.appyx.dagger.hilt.generator.generateHiltModule(
            codeGenerator = codeGenerator,
            declaration = declaration,
            hiltModuleTypeSpecBuilder = createDaggerBindsModuleTypeSpec(
                declaration = declaration,
                moduleSuffix = "NodeFactoryModule",
                returnType = nodeFactoryClassName.parameterizedBy(STAR)
            )
        )
    }
}

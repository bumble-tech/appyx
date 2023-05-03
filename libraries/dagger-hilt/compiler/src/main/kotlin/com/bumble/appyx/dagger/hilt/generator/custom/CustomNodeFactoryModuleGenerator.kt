package com.bumble.appyx.dagger.hilt.generator.custom

import com.bumble.appyx.dagger.hilt.generator.ModuleGenerator
import com.bumble.appyx.dagger.hilt.generator.createDaggerBindsModuleTypeSpec
import com.bumble.appyx.dagger.hilt.internal.CustomNodeFactoryQualifier
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ANY

/**
 * Source generator to support Hilt injection of NodeFactory using @HiltCustomNodeFactory.
 *
 * The following inputs:
 *
 * internal class SampleNode(buildContext: BuildContext, ...) : Node(buildContext)
 *
 * interface SampleNodeNodeFactory {
 *   fun build(buildContext: BuildContext, ...): Node
 * }
 *
 * @HiltCustomNodeFactory(SampleNodeNodeFactory::class)
 * internal class SampleNodeNodeFactoryImpl @Inject constructor() : SampleNodeNodeFactory {
 *   override fun build(buildContext: BuildContext, ...) = SampleNode(...)
 * }
 *
 * We will generate:
 * ```
 * @Module
 * @InstallIn(ActivityComponent::class)
 * @OriginatingElement(topLevelClass = SampleNodeNodeFactoryImpl::class)
 * internal interface SampleNodeNodeFactoryImpl_CustomNodeFactoryModule {
 *   @Binds
 *   @IntoMap
 *   @ClassKey(value = SampleNodeNodeFactory::class)
 *   @CustomNodeFactoryQualifier
 *   fun binds(factory: SampleNodeNodeFactoryImpl): Any
 * }
 * ```
 */
internal object CustomNodeFactoryModuleGenerator : ModuleGenerator {

    override fun generateHiltModule(
        codeGenerator: CodeGenerator,
        declaration: KSClassDeclaration
    ) {
        com.bumble.appyx.dagger.hilt.generator.generateHiltModule(
            codeGenerator = codeGenerator,
            declaration = declaration,
            hiltModuleTypeSpecBuilder = createDaggerBindsModuleTypeSpec(
                declaration = declaration,
                moduleSuffix = "CustomNodeFactoryModule",
                returnType = ANY,
                functionFunc = { funSpecBuilder ->
                    // Add a qualifier as otherwise we have a map of 'Any's which could conflict with other hilt logic.
                    funSpecBuilder.addAnnotation(CustomNodeFactoryQualifier::class.java)
                }
            )
        )
    }
}

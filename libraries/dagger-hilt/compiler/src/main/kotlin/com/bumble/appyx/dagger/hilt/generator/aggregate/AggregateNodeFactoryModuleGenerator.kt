package com.bumble.appyx.dagger.hilt.generator.aggregate

import com.bumble.appyx.dagger.hilt.generator.APPYX_HILT_INTERNAL_PACKAGE
import com.bumble.appyx.dagger.hilt.generator.APPYX_HILT_PACKAGE
import com.bumble.appyx.dagger.hilt.generator.ModuleGenerator
import com.bumble.appyx.dagger.hilt.generator.addIntoMapAnnotations
import com.bumble.appyx.dagger.hilt.generator.requireClassDeclarationByName
import com.bumble.appyx.dagger.hilt.generator.requireContainingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import dagger.Provides

/**
 * Source generator to support Hilt injection of NodeFactory using @HiltAggregateNodeFactory.
 *
 * The following inputs:
 *
 * class ExampleParentNode {
 *   @HiltAggregateNodeFactory
 *   interface AggregateNodeFactory {
 *     val exampleNodeFactory: NodeFactory<ExampleNodeFactory>
 *     val exampleCustomNodeFactory: ExampleCustomNodeFactory
 *   }
 * }
 *
 * We will generate:
 * ```
 * @Module
 * @InstallIn(ActivityComponent::class)
 * @OriginatingElement(topLevelClass = ExampleParentNode::class)
 * internal object AggregateNodeFactory_AggregateNodeFactoryModule {
 *   @Provides
 *   @IntoMap
 *   @ClassKey(value = ExampleParentNode.AggregateNodeFactory::class)
 *   internal fun provides(): AggregateNodeFactoryBuilder<*> = DaggerAggregateNodeFactoryBuilder()
 *
 *   internal class DaggerAggregateNodeFactory(
 *     public override val nodeFactoryProvider: NodeFactoryProvider,
 *   ) : ExampleParentNode.AggregateNodeFactory, com.bumble.appyx.dagger.hilt.internal.AggregateNodeFactory {
 *     public override val exampleNodeFactory: NodeFactory<ExampleNodeFactory>
 *         by nodeFactory<ExampleNodeFactory>()
 *
 *     public override val exampleCustomNodeFactory: ExampleCustomNodeFactory by
 *         customNodeFactory<ExampleCustomNodeFactory>()
 *   }
 *
 *   internal class DaggerAggregateNodeFactoryBuilder :
 *       AggregateNodeFactoryBuilder<DaggerAggregateNodeFactory> {
 *     public override fun build(nodeFactoryProvider: NodeFactoryProvider): DaggerAggregateNodeFactory
 *         = DaggerAggregateNodeFactory(nodeFactoryProvider)
 *   }
 * }
 * ```
 */
internal class AggregateNodeFactoryModuleGenerator(
    private val resolver: Resolver
) : ModuleGenerator {
    private val aggregateNodeFactoryBuilderClassName = ClassName(
        APPYX_HILT_INTERNAL_PACKAGE, "AggregateNodeFactoryBuilder"
    )
    private val nodeFactoryProviderClassName = ClassName(APPYX_HILT_PACKAGE, "NodeFactoryProvider")

    override fun generateHiltModule(
        codeGenerator: CodeGenerator,
        declaration: KSClassDeclaration
    ) {
        val declarationClassName = declaration.toClassName()
        val packageName = declarationClassName.packageName
        val moduleName = "${declarationClassName.simpleName}_AggregateNodeFactoryModule"

        com.bumble.appyx.dagger.hilt.generator.generateHiltModule(
            codeGenerator = codeGenerator,
            declaration = declaration,
            hiltModuleTypeSpecBuilder = createHiltModuleSpecBuilder(
                declaration = declaration,
                bindingClassName = declarationClassName,
                moduleClassName = ClassName(packageName, moduleName)
            )
        )
    }

    private fun createHiltModuleSpecBuilder(
        declaration: KSClassDeclaration,
        bindingClassName: ClassName,
        moduleClassName: ClassName
    ): TypeSpec.Builder {
        val nodeFactoryClassName = "Dagger${bindingClassName.simpleName}"
        val nodeFactoryBuilderClassName = "${nodeFactoryClassName}Builder"
        return TypeSpec.objectBuilder(moduleClassName)
            .addFunction(
                FunSpec.builder("provides")
                    .addAnnotation(Provides::class.java)
                    .addIntoMapAnnotations(
                        classKeyType = bindingClassName,
                        returnType = aggregateNodeFactoryBuilderClassName.parameterizedBy(STAR)
                    )
                    .addModifiers(KModifier.INTERNAL)
                    .addCode("return $nodeFactoryBuilderClassName()")
                    .build()
            )
            .addType(
                createNodeFactoryProviderSpec(
                    nodeFactoryClassName = nodeFactoryClassName,
                    typeElement = declaration,
                    classKeyClassName = bindingClassName
                )
            )
            .addType(
                createNodeFactoryProviderBuilderSpec(
                    nodeFactoryBuilderClassName = nodeFactoryBuilderClassName,
                    typeElement = declaration,
                    factoryProviderClassName = moduleClassName.nestedClass(nodeFactoryClassName)
                )
            )
    }

    private fun createNodeFactoryProviderSpec(
        nodeFactoryClassName: String,
        typeElement: KSClassDeclaration,
        classKeyClassName: TypeName
    ): TypeSpec =
        TypeSpec.classBuilder(nodeFactoryClassName)
            .addOriginatingKSFile(typeElement.requireContainingFile())
            .addSuperinterface(classKeyClassName)
            .addSuperinterface(ClassName.bestGuess("$APPYX_HILT_INTERNAL_PACKAGE.AggregateNodeFactory"))
            .addModifiers(KModifier.INTERNAL)
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter(NODE_FACTORY_PROVIDER_PARAM, nodeFactoryProviderClassName)
                    .build()
            )
            .addProperty(
                PropertySpec.builder(NODE_FACTORY_PROVIDER_PARAM, nodeFactoryProviderClassName, KModifier.OVERRIDE)
                    .initializer(NODE_FACTORY_PROVIDER_PARAM)
                    .build()
            )
            .apply {
                typeElement.getAllProperties()
                    .forEach { property ->
                        addProperty(createPropertySpecBuilder(property).build())
                    }
            }
            .build()

    private fun createPropertySpecBuilder(property: KSPropertyDeclaration): PropertySpec.Builder {
        val nodeFactoryType =
            resolver
                .requireClassDeclarationByName("com.bumble.appyx.core.integration.NodeFactory")
                .asStarProjectedType()
        val actualType = property.type.resolve().starProjection()
        val inheritsNodeFactory = actualType.isAssignableFrom(nodeFactoryType)
        val type = property.type.toTypeName()

        return PropertySpec
            .builder(
                property.simpleName.getShortName(),
                type,
                KModifier.OVERRIDE
            )
            .run {
                if (inheritsNodeFactory) {
                    delegate(
                        CodeBlock.of(
                            "%M<%T>()",
                            MemberName(APPYX_HILT_INTERNAL_PACKAGE, "nodeFactory"),
                            (type as ParameterizedTypeName).typeArguments.first()
                        )
                    )
                } else {
                    delegate(
                        CodeBlock.of(
                            "%M<%T>()",
                            MemberName(APPYX_HILT_INTERNAL_PACKAGE, "customNodeFactory"),
                            type
                        )
                    )
                }
            }
    }

    private fun createNodeFactoryProviderBuilderSpec(
        nodeFactoryBuilderClassName: String,
        typeElement: KSClassDeclaration,
        factoryProviderClassName: TypeName,
    ): TypeSpec =
        TypeSpec.classBuilder(nodeFactoryBuilderClassName)
            .addOriginatingKSFile(typeElement.requireContainingFile())
            .addSuperinterface(this.aggregateNodeFactoryBuilderClassName.parameterizedBy(factoryProviderClassName))
            .addModifiers(KModifier.INTERNAL)
            .addFunction(
                FunSpec.builder("build")
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(factoryProviderClassName)
                    .addParameter(NODE_FACTORY_PROVIDER_PARAM, nodeFactoryProviderClassName)
                    .addCode("return %T(nodeFactoryProvider)", factoryProviderClassName)
                    .build()
            )
            .build()

    private companion object {
        private const val NODE_FACTORY_PROVIDER_PARAM = "nodeFactoryProvider"
    }
}

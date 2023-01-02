package com.bumble.appyx.dagger.hilt.generator.node

import com.bumble.appyx.dagger.hilt.generator.ModuleGenerator
import com.bumble.appyx.dagger.hilt.generator.addIntoMapAnnotations
import com.bumble.appyx.dagger.hilt.generator.getAnnotationClassNameArgument
import com.bumble.appyx.dagger.hilt.generator.nodeFactoryClassName
import com.bumble.appyx.dagger.hilt.generator.requireContainingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import dagger.Provides

/**
 * Source generator to support Hilt injection of NodeFactory using @HiltNode.
 *
 * The following inputs:
 *
 * open class SampleNode(buildContext: BuildContext) : Node(buildContext)
 *
 * @HiltNode(SampleNode::class)
 * internal class SampleNodeImpl(buildContext: BuildContext) : SampleNode(buildContext)
 *
 * We will generate:
 * ```
 * @Module
 * @InstallIn(ActivityComponent::class)
 * @OriginatingElement(topLevelClass = SampleNodeImpl::class)
 * internal object SampleNodeImpl_NodeFactoryModule {
 *   @Provides
 *   @IntoMap
 *   @ClassKey(value = SampleNode::class)
 *   internal fun provides(): NodeFactory<*> = SampleNodeNodeFactory()
 *
 *   internal class SampleNodeNodeFactory : NodeFactory<SampleNode> {
 *      override fun create(buildContext: BuildContext): SampleNode =
 *         SampleNodeImpl(buildContext)
 *   }
 * }
 * ```
 */
internal object NodeModuleGenerator : ModuleGenerator {
    override fun generateHiltModule(
        codeGenerator: CodeGenerator,
        declaration: KSClassDeclaration
    ) {
        val declarationClassName = declaration.toClassName()
        val packageName = declarationClassName.packageName
        val moduleName = "${declarationClassName.simpleName}_NodeFactoryModule"

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
        val classKeyClassName = declaration.getAnnotationClassNameArgument(classNameIfUndefined = bindingClassName)

        val nodeFactoryImplementationClassName = "${classKeyClassName.simpleName}NodeFactory"
        return TypeSpec.objectBuilder(moduleClassName)
            .addFunction(
                FunSpec.builder("provides")
                    .addAnnotation(Provides::class.java)
                    .addIntoMapAnnotations(
                        classKeyType = classKeyClassName,
                        returnType = nodeFactoryClassName.parameterizedBy(STAR)
                    )
                    .addModifiers(KModifier.INTERNAL)
                    .addCode("return $nodeFactoryImplementationClassName()")
                    .build()
            )
            .addType(
                createNodeFactorySpec(
                    nodeFactoryImplementationClassName = nodeFactoryImplementationClassName,
                    typeElement = declaration,
                    nodeClassName = bindingClassName,
                    classKeyClassName = classKeyClassName
                )
            )
    }

    private fun createNodeFactorySpec(
        nodeFactoryImplementationClassName: String,
        typeElement: KSClassDeclaration,
        nodeClassName: ClassName,
        classKeyClassName: TypeName
    ): TypeSpec =
        TypeSpec.classBuilder(nodeFactoryImplementationClassName)
            .addOriginatingKSFile(typeElement.requireContainingFile())
            .addSuperinterface(nodeFactoryClassName.parameterizedBy(classKeyClassName))
            .addModifiers(KModifier.INTERNAL)
            .addFunction(
                FunSpec.builder("create")
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(classKeyClassName)
                    .addParameter(
                        "buildContext",
                        ClassName(
                            "com.bumble.appyx.core.modality", "BuildContext"
                        )
                    )
                    .addStatement("return %T(buildContext)", nodeClassName)
                    .build()
            )
            .build()
}

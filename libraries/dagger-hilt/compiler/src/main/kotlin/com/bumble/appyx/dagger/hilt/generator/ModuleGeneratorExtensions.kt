package com.bumble.appyx.dagger.hilt.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.codegen.OriginatingElement
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

internal fun generateHiltModule(
    codeGenerator: CodeGenerator,
    declaration: KSClassDeclaration,
    hiltModuleTypeSpecBuilder: TypeSpec.Builder
) {
    val typeSpec = hiltModuleTypeSpecBuilder
        .addCommonHiltModuleAnnotations(declaration)
        .build()
    val fileName = requireNotNull(typeSpec.name) {
        "createTypeSpecBuilder returned an anonymous class"
    }
    FileSpec.builder(declaration.packageName.asString(), fileName)
        .addType(typeSpec)
        .build()
        .writeTo(
            codeGenerator,
            Dependencies(false, declaration.requireContainingFile())
        )
}

internal fun createDaggerBindsModuleTypeSpec(
    declaration: KSClassDeclaration,
    moduleSuffix: String,
    returnType: TypeName,
    functionFunc: (FunSpec.Builder) -> FunSpec.Builder = { it }
): TypeSpec.Builder =
    declaration.toClassName().let { declarationClassName ->
        TypeSpec
            .interfaceBuilder("${declarationClassName.simpleName}_$moduleSuffix")
            .addFunction(
                FunSpec.builder("binds")
                    .addAnnotation(Binds::class.java)
                    .addIntoMapAnnotations(
                        classKeyType = declaration
                            .getAnnotationClassNameArgument(classNameIfUndefined = declarationClassName),
                        returnType = returnType
                    )
                    .addModifiers(KModifier.ABSTRACT)
                    .addParameter("factory", declarationClassName)
                    .let(functionFunc)
                    .build()
            )
    }

/**
 * These annotations must be added to the TypeSpec of all the generated modules.
 */
private fun TypeSpec.Builder.addCommonHiltModuleAnnotations(
    declaration: KSClassDeclaration
): TypeSpec.Builder =
    addOriginatingKSFile(declaration.requireContainingFile())
        .addAnnotation(Module::class.java)
        .addAnnotation(
            AnnotationSpec.builder(InstallIn::class.java)
                .addMember(
                    "%T::class",
                    ClassName("dagger.hilt.android.components", "ActivityComponent")
                )
                .build()
        )
        .addAnnotation(
            AnnotationSpec.builder(OriginatingElement::class.java)
                .addMember(
                    "topLevelClass = %T::class",
                    declaration.toClassName().topLevelClassName()
                )
                .build()
        )
        .addModifiers(KModifier.INTERNAL)

internal fun FunSpec.Builder.addIntoMapAnnotations(
    classKeyType: TypeName,
    returnType: TypeName
): FunSpec.Builder =
    addAnnotation(IntoMap::class.java)
        .addAnnotation(
            AnnotationSpec.builder(ClassKey::class.java)
                .addMember("value = %T::class", classKeyType)
                .build()
        )
        .returns(returnType)

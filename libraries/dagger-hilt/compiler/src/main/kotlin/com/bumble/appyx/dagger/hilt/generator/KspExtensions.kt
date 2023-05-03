package com.bumble.appyx.dagger.hilt.generator

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

/**
 * At the moment all of our annotations only have one argument.
 */
internal fun KSClassDeclaration.getAnnotationClassNameArgument(classNameIfUndefined: ClassName): ClassName =
    (annotations.first().arguments.first().value as? KSType)
        ?.toClassName()
        ?.takeIf { it != ANY }
        ?: classNameIfUndefined

internal fun KSDeclaration.requireContainingFile(): KSFile =
    requireNotNull(containingFile) {
        "We do not currently support symbols that don't come from a source file"
    }

internal fun Resolver.requireClassDeclarationByName(name: String): KSClassDeclaration =
    requireNotNull(getClassDeclarationByName(name)) {
        "Unable to find class declaration '$name'"
    }

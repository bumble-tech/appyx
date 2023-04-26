package com.bumble.appyx.interactions.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlin.reflect.KClass

fun <T : Any> Resolver.getClassName(clazz: KClass<T>) =
    getClassDeclarationByName(getKSNameFromString(clazz.qualifiedName!!))!!
        .toClassName()

fun <T : Any> Resolver.getType(clazz: KClass<T>) =
    getClassDeclarationByName(getKSNameFromString(clazz.qualifiedName!!))!!
        .asStarProjectedType()

fun <T : Any> Resolver.getType(clazz: KClass<T>, types: List<KSTypeArgument>) =
    getClassDeclarationByName(getKSNameFromString(clazz.qualifiedName!!))!!
        .asType(types)

fun <T : Any> Resolver.getTypeName(clazz: KClass<T>) =
    getType(clazz).toTypeName()

fun <T : Any> Resolver.getTypeName(clazz: KClass<T>, types: List<KSTypeArgument>) =
    getType(clazz, types).toTypeName()

fun <T : Any> Resolver.getSymbolsWithAnnotation(clazz: KClass<T>) =
    getSymbolsWithAnnotation(clazz.qualifiedName!!)

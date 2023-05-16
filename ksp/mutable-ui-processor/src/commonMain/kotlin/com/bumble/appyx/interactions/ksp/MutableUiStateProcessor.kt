package com.bumble.appyx.interactions.ksp

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.Modifier
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.property.MotionProperty
import com.bumble.appyx.interactions.core.ui.state.BaseMutableUiState
import com.bumble.appyx.interactions.core.ui.state.MutableUiStateSpecs
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.kspDependencies
import com.squareup.kotlinpoet.ksp.originatingKSFiles
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.coroutines.CoroutineScope

class MutableUiStateProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val resultFiles = mutableListOf<KSFile>()
        val targetType = resolver.getType(MotionProperty.Target::class)
        val symbols = resolver.getSymbolsWithAnnotation(MutableUiStateSpecs::class)
        symbols.forEach { symbol ->
            if (isValidAnnotatedSymbol(symbol)) {
                val playMode = getPlayMode(resolver, symbol)
                val classDeclaration = symbol as KSClassDeclaration
                val classTypeName = resolver.createKSTypeReferenceFromKSType(classDeclaration.asStarProjectedType()).toTypeName()
                val params = classDeclaration.extractTargetTypeParams(targetType)
                if (params.isNotEmpty()) {
                    resolver.generateMutableUiStateFile(classDeclaration, classTypeName, playMode, params).apply {
                        writeTo(codeGenerator, kspDependencies(true))
                        resultFiles.addAll(this.originatingKSFiles())
                    }
                } else {
                    logger.error("Class constructor requires at least one value derived from MotionProperty.Target", symbol)
                }
            } else {
                logger.error("MutableUiStateSpecs annotation should only be used with classes.", symbol)
            }
        }
        return resolver.getAllFiles().filterNot { it.validate() }.toList()
    }

    private fun isValidAnnotatedSymbol(symbol: KSAnnotated) =
        symbol is KSClassDeclaration

    private fun getPlayMode(resolver: Resolver, symbol: KSAnnotated): MutableUiStateSpecs.PlayMode {
        val playModeType = resolver.getType(MutableUiStateSpecs.PlayMode::class)
        val playModes = symbol
            .annotations
            .flatMap { it.arguments }
            .filter { it.value is KSType }
            .filter { playModeType.isAssignableFrom(it.value as KSType) }
            .mapNotNull { it.value?.toString() }
        return when {
            playModes.count() == 1 && playModes.first().contains(PLAY_MODE_SEQUENCE) -> MutableUiStateSpecs.PlayMode.SequenceMode
            playModes.count() == 1 && playModes.first().contains(PLAY_MODE_CONCURRENT) -> MutableUiStateSpecs.PlayMode.ConcurrentMode
            else -> MutableUiStateSpecs.PlayMode.ConcurrentMode
        }
    }

    private fun KSClassDeclaration.extractTargetTypeParams(targetType: KSType) =
        getPrimaryConstructorParams()
            .map { it.name?.asString()!! to it.type.resolve() }
            .filter { targetType.isAssignableFrom(it.second) }
            .map { it.first to (it.second.declaration.parentDeclaration as KSClassDeclaration).asStarProjectedType().toTypeName() }
            .map { ParameterSpec.builder(it.first, it.second).build() }

    private fun KSClassDeclaration.getPrimaryConstructorParams() =
        primaryConstructor?.parameters ?: emptyList()

    private fun Resolver.generateMutableUiStateFile(
        classDeclaration: KSClassDeclaration,
        classTypeName: TypeName,
        playMode: MutableUiStateSpecs.PlayMode,
        params: List<ParameterSpec>
    ): FileSpec =
        FileSpec.builder(classDeclaration.packageName.asString(), MUTABLE_UI_STATE)
            .addFileComment(GENERATED_COMMENT)
            .addImport(IMPORT_COROUTINES, IMPORT_ASYNC, IMPORT_AWAIT_ALL, IMPORT_LAUNCH)
            .addImport(IMPORT_COMPOSE_ANIMATION_CORE, IMPORT_SPRING_SPEC, IMPORT_SPRING)
            .addType(generateMutableUiStateType(classDeclaration, classTypeName, playMode, params))
            .build()

    private fun Resolver.generateMutableUiStateType(
        classDeclaration: KSClassDeclaration,
        classTypeName: TypeName,
        playMode: MutableUiStateSpecs.PlayMode,
        params: List<ParameterSpec>
    ): TypeSpec =
        TypeSpec.classBuilder(MUTABLE_UI_STATE)
            .addModifiers(KModifier.FINAL)
            .superclass(generateSuperclass(classDeclaration, classTypeName))
            .primaryConstructor(generatePrimaryConstructor(params))
            .addSuperclassConstructorParameter(generateSuperClassConstructorParameter(params))
            .addProperties(params.toProperties())
            .addProperty(generateModifierProperty(params))
            .addFunction(generateAnimateToFunction(classTypeName, playMode, params))
            .addFunction(generateSnapToFunction(classTypeName, params))
            .addFunction(generateLerpToFunction(classTypeName, params))
            .build()

    private fun Resolver.generateSuperclass(classDeclaration: KSClassDeclaration, classTypeName: TypeName) =
        getType(BaseMutableUiState::class)
            .toClassName()
            .parameterizedBy(
                ClassName(classDeclaration.packageName.asString(), MUTABLE_UI_STATE),
                classTypeName,
            )

    private fun Resolver.generatePrimaryConstructor(params: List<ParameterSpec>): FunSpec =
        FunSpec.constructorBuilder()
            .addParameter(PARAM_UI_CONTEXT, getTypeName(UiContext::class))
            .addParameters(params)
            .build()

    private fun generateSuperClassConstructorParameter(params: List<ParameterSpec>) =
        CodeBlock.Builder()
            .addStatement("")
            .indent()
            .addStatement("$PARAM_UI_CONTEXT = $PARAM_UI_CONTEXT,")
            .addStatement("$PARAM_MOTION_PROPERTIES = listOf(${params.joinToString { it.name }}),")
            .unindent()
            .build()

    private fun List<ParameterSpec>.toProperties() =
        map {
            PropertySpec.builder(it.name, it.type)
                .initializer(it.name)
                .build()
        }

    private fun generateModifierProperty(params: List<ParameterSpec>) =
        PropertySpec.builder(PROPERTY_MODIFIER, Modifier::class, KModifier.OVERRIDE)
            .initializer(
                with(CodeBlock.Builder()) {
                    addStatement("Modifier")
                    indent()
                    params.forEach { addStatement(".then(${it.name}.modifier)") }
                    unindent()
                    build()
                }
            )
            .build()

    private fun Resolver.generateAnimateToFunction(
        classTypeName: TypeName,
        playMode: MutableUiStateSpecs.PlayMode,
        params: List<ParameterSpec>,
    ) = FunSpec.builder(FUNCTION_ANIMATE_TO)
        .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
        .addParameter(PARAM_SCOPE, getTypeName(CoroutineScope::class))
        .addParameter(PARAM_TARGET, classTypeName)
        .addParameter(PARAM_SPRING_SPEC, getClassName(SpringSpec::class).parameterizedBy(getTypeName(Float::class)))
        .addCode(generateAnimateToCodeBlock(playMode, params))
        .build()

    private fun generateAnimateToCodeBlock(
        playMode: MutableUiStateSpecs.PlayMode,
        params: List<ParameterSpec>,
    ) = when (playMode) {
        MutableUiStateSpecs.PlayMode.SequenceMode -> generateSequentialAnimateToCodeBlock(params)
        MutableUiStateSpecs.PlayMode.ConcurrentMode -> generateConcurrentAnimateToCodeBlock(params)
    }

    private fun generateSequentialAnimateToCodeBlock(params: List<ParameterSpec>) =
        with(CodeBlock.Builder()) {
            params.forEach {
                addStatement("${it.name}.$FUNCTION_ANIMATE_TO(")
                indent()
                addStatement("$PARAM_TARGET.${it.name}.value,")
                addStatement("spring($PARAM_SPRING_SPEC.dampingRatio, $PARAM_SPRING_SPEC.stiffness),")
                unindent()
                addStatement(")")
            }
            build()
        }

    private fun generateConcurrentAnimateToCodeBlock(params: List<ParameterSpec>) =
        with(CodeBlock.Builder()) {
            indent()
            addStatement("listOf(")
            params.forEach {
                indent()
                addStatement("$PARAM_SCOPE.async {")
                indent()
                addStatement("${it.name}.$FUNCTION_ANIMATE_TO(")
                indent()
                addStatement("$PARAM_TARGET.${it.name}.value,")
                addStatement("spring($PARAM_SPRING_SPEC.dampingRatio, $PARAM_SPRING_SPEC.stiffness),")
                unindent()
                addStatement(")")
                unindent()
                addStatement("},")
                unindent()
            }
            addStatement(").awaitAll()")
            unindent()
            build()
        }

    private fun Resolver.generateSnapToFunction(classTypeName: TypeName, params: List<ParameterSpec>) =
        FunSpec.builder(FUNCTION_SNAP_TO)
            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
            .addParameter(PARAM_SCOPE, getTypeName(CoroutineScope::class))
            .addParameter(PARAM_TARGET, classTypeName)
            .addCode(generateSnapToCodeBlock(params))
            .build()

    private fun generateSnapToCodeBlock(params: List<ParameterSpec>) =
        with(CodeBlock.Builder()) {
            params.forEach {
                addStatement("${it.name}.$FUNCTION_SNAP_TO($PARAM_TARGET.${it.name}.value)")
            }
            build()
        }

    private fun Resolver.generateLerpToFunction(classTypeName: TypeName, params: List<ParameterSpec>) =
        FunSpec.builder(FUNCTION_LERP_TO)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(PARAM_SCOPE, getTypeName(CoroutineScope::class))
            .addParameter(PARAM_START, classTypeName)
            .addParameter(PARAM_END, classTypeName)
            .addParameter(PARAM_FRACTION, getTypeName(Float::class))
            .addCode(generateLerpToCodeBlock(params))
            .build()

    private fun generateLerpToCodeBlock(params: List<ParameterSpec>) =
        with(CodeBlock.Builder()) {
            addStatement("$PARAM_SCOPE.launch {")
            indent()
            params.forEach {
                addStatement("${it.name}.$FUNCTION_LERP_TO($PARAM_START.${it.name}, $PARAM_END.${it.name}, $PARAM_FRACTION)")
            }
            unindent()
            addStatement("}")
            build()
        }

    private companion object {
        const val MUTABLE_UI_STATE = "MutableUiState"

        const val PLAY_MODE_SEQUENCE = "SequenceMode"
        const val PLAY_MODE_CONCURRENT = "ConcurrentMode"

        const val GENERATED_COMMENT = "Generated file ... DO NOT EDIT!"

        const val IMPORT_COROUTINES = "kotlinx.coroutines"
        const val IMPORT_ASYNC = "async"
        const val IMPORT_AWAIT_ALL = "awaitAll"
        const val IMPORT_LAUNCH = "launch"

        const val IMPORT_COMPOSE_ANIMATION_CORE = "androidx.compose.animation.core"
        const val IMPORT_SPRING_SPEC = "SpringSpec"
        const val IMPORT_SPRING = "spring"

        const val PARAM_UI_CONTEXT = "uiContext"
        const val PARAM_MOTION_PROPERTIES = "motionProperties"
        const val PARAM_SCOPE = "scope"
        const val PARAM_TARGET = "target"
        const val PARAM_SPRING_SPEC = "springSpec"
        const val PARAM_START = "start"
        const val PARAM_END = "end"
        const val PARAM_FRACTION = "fraction"

        const val PROPERTY_MODIFIER = "modifier"

        const val FUNCTION_ANIMATE_TO = "animateTo"
        const val FUNCTION_SNAP_TO = "snapTo"
        const val FUNCTION_LERP_TO = "lerpTo"
    }

}

class MutableUiStateProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        MutableUiStateProcessor(environment.codeGenerator, environment.logger)
}

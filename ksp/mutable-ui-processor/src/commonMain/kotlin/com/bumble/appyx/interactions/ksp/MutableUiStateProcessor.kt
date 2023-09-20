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

@Suppress("TooManyFunctions", "MaxLineLength")
class MutableUiStateProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    @Suppress("NestedBlockDepth")
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
                        writeTo(codeGenerator, kspDependencies(true, listOf(symbol.containingFile!!)))
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

    private fun getPlayMode(resolver: Resolver, symbol: KSAnnotated): MutableUiStateSpecs.AnimationMode {
        val animationModeType = resolver.getType(MutableUiStateSpecs.AnimationMode::class)
        val playModes = symbol
            .annotations
            .flatMap { it.arguments }
            .filter { it.value is KSType }
            .filter { animationModeType.isAssignableFrom(it.value as KSType) }
            .mapNotNull { it.value?.toString() }
        return when {
            playModes.count() == 1 && playModes.first().contains(PLAY_MODE_SEQUENTIAL) -> MutableUiStateSpecs.AnimationMode.SEQUENTIAL
            playModes.count() == 1 && playModes.first().contains(PLAY_MODE_CONCURRENT) -> MutableUiStateSpecs.AnimationMode.CONCURRENT
            else -> MutableUiStateSpecs.AnimationMode.CONCURRENT
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
        animationMode: MutableUiStateSpecs.AnimationMode,
        params: List<ParameterSpec>
    ): FileSpec =
        FileSpec.builder(classDeclaration.packageName.asString(), MUTABLE_UI_STATE)
            .addFileComment(GENERATED_COMMENT)
            .addImport(IMPORT_COROUTINES, IMPORT_ASYNC, IMPORT_AWAIT_ALL, IMPORT_LAUNCH)
            .addImport(IMPORT_COMPOSE_ANIMATION_CORE, IMPORT_SPRING_SPEC, IMPORT_SPRING)
            .addType(generateMutableUiStateType(classTypeName, animationMode, params))
            .addFunction(generateToMutableStateFunction(classDeclaration, classTypeName, params))
            .build()

    private fun Resolver.generateMutableUiStateType(
        classTypeName: TypeName,
        animationMode: MutableUiStateSpecs.AnimationMode,
        params: List<ParameterSpec>
    ): TypeSpec =
        TypeSpec.classBuilder(MUTABLE_UI_STATE)
            .superclass(generateSuperclass(classTypeName))
            .primaryConstructor(generatePrimaryConstructor(params))
            .addSuperclassConstructorParameter(generateSuperClassConstructorParameter(params))
            .addProperties(params.toProperties())
            .addProperty(generateModifierProperty(params))
            .addFunction(generateAnimateToFunction(classTypeName, animationMode, params))
            .addFunction(generateSnapToFunction(classTypeName, params))
            .addFunction(generateLerpToFunction(classTypeName, params))
            .build()

    private fun Resolver.generateSuperclass(classTypeName: TypeName) =
        getType(BaseMutableUiState::class)
            .toClassName()
            .parameterizedBy(
                classTypeName,
            )

    private fun Resolver.generatePrimaryConstructor(params: List<ParameterSpec>): FunSpec =
        FunSpec.constructorBuilder()
            .addParameter(PARAM_UI_CONTEXT, getTypeName(UiContext::class))
            .addParameters(params)
            .build()

    private fun generateSuperClassConstructorParameter(params: List<ParameterSpec>) =
        CodeBlock.builder()
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
        PropertySpec.builder(COMBINED_MOTION_PROPERTY_MODIFIER, Modifier::class, KModifier.OVERRIDE)
            .initializer(
                with(CodeBlock.builder()) {
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
        animationMode: MutableUiStateSpecs.AnimationMode,
        params: List<ParameterSpec>,
    ) = FunSpec.builder(FUNCTION_ANIMATE_TO)
        .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
        .addParameter(PARAM_SCOPE, getTypeName(CoroutineScope::class))
        .addParameter(PARAM_TARGET, classTypeName)
        .addParameter(PARAM_SPRING_SPEC, getClassName(SpringSpec::class).parameterizedBy(getTypeName(Float::class)))
        .addCode(generateAnimateToCodeBlock(animationMode, params))
        .build()

    private fun generateAnimateToCodeBlock(
        animationMode: MutableUiStateSpecs.AnimationMode,
        params: List<ParameterSpec>,
    ) = when (animationMode) {
        MutableUiStateSpecs.AnimationMode.SEQUENTIAL -> generateSequentialAnimateToCodeBlock(params)
        MutableUiStateSpecs.AnimationMode.CONCURRENT -> generateConcurrentAnimateToCodeBlock(params)
    }

    private fun generateSequentialAnimateToCodeBlock(params: List<ParameterSpec>) =
        with(CodeBlock.builder()) {
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
        with(CodeBlock.builder()) {
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

    private fun generateSnapToFunction(classTypeName: TypeName, params: List<ParameterSpec>) =
        FunSpec.builder(FUNCTION_SNAP_TO)
            .addModifiers(KModifier.OVERRIDE, KModifier.SUSPEND)
            .addParameter(PARAM_TARGET, classTypeName)
            .addCode(generateSnapToCodeBlock(params))
            .build()

    private fun generateSnapToCodeBlock(params: List<ParameterSpec>) =
        with(CodeBlock.builder()) {
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
        with(CodeBlock.builder()) {
            addStatement("$PARAM_SCOPE.launch {")
            indent()
            params.forEach {
                addStatement("${it.name}.$FUNCTION_LERP_TO($PARAM_START.${it.name}, $PARAM_END.${it.name}, $PARAM_FRACTION)")
            }
            unindent()
            addStatement("}")
            build()
        }

    private fun Resolver.generateToMutableStateFunction(classDeclaration: KSClassDeclaration, classTypeName: TypeName, params: List<ParameterSpec>) =
        FunSpec.builder(FUNCTION_TO_MUTABLE_STATE)
            .receiver(classTypeName)
            .returns(ClassName(classDeclaration.packageName.asString(), MUTABLE_UI_STATE))
            .addParameter(PARAM_UI_CONTEXT, getTypeName(UiContext::class))
            .addCode(generateToMutableStateCode(params))
            .build()

    private fun generateToMutableStateCode(params: List<ParameterSpec>) =
        with(CodeBlock.builder()) {
            add("return $MUTABLE_UI_STATE(\n")
            indent()
            addStatement("$PARAM_UI_CONTEXT = $PARAM_UI_CONTEXT,")
            params.forEach {
                addStatement("${it.name} = ${it.type}($PARAM_UI_CONTEXT.$PARAM_COROUTINE_SCOPE, ${it.name}),")
            }
            unindent()
            add(")")
            build()
        }


    private companion object {
        const val MUTABLE_UI_STATE = "MutableUiState"

        const val PLAY_MODE_SEQUENTIAL = "SequentialMode"
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
        const val PARAM_COROUTINE_SCOPE = "coroutineScope"
        const val PARAM_SCOPE = "scope"
        const val PARAM_TARGET = "target"
        const val PARAM_SPRING_SPEC = "springSpec"
        const val PARAM_START = "start"
        const val PARAM_END = "end"
        const val PARAM_FRACTION = "fraction"

        const val COMBINED_MOTION_PROPERTY_MODIFIER = "combinedMotionPropertyModifier"

        const val FUNCTION_ANIMATE_TO = "animateTo"
        const val FUNCTION_SNAP_TO = "snapTo"
        const val FUNCTION_LERP_TO = "lerpTo"
        const val FUNCTION_TO_MUTABLE_STATE = "toMutableState"
    }

}

class MutableUiStateProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        MutableUiStateProcessor(environment.codeGenerator, environment.logger)
}

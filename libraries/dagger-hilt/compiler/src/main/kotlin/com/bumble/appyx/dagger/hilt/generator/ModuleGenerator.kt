package com.bumble.appyx.dagger.hilt.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal interface ModuleGenerator {
    fun generateHiltModule(
        codeGenerator: CodeGenerator,
        declaration: KSClassDeclaration
    )
}

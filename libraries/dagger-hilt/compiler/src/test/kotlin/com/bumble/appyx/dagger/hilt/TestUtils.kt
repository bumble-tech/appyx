package com.bumble.appyx.dagger.hilt

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions
import java.io.File

fun verifyFileGenerated(
    compilation: KotlinCompilation,
    fileName: String,
    content: String,
) {
    Assertions.assertEquals(KotlinCompilation.ExitCode.OK, compilation.compile().exitCode)
    compilation.kspSourcesDir.walkTopDown()
        .single { it.nameWithoutExtension == fileName }
        .let { generatedFile ->
            Assertions.assertEquals(content, generatedFile.readText())
        }
}

fun prepareCompilation(temporaryFolder: File, vararg sourceFiles: SourceFile) =
    KotlinCompilation().apply {
        workingDir = temporaryFolder
        inheritClassPath = true
        symbolProcessorProviders = listOf(AppyxHiltProcessorProvider())
        sources = sourceFiles.asList()
        verbose = false
        kspIncremental = true
    }

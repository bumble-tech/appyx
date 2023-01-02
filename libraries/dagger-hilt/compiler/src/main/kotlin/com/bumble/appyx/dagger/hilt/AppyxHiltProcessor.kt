package com.bumble.appyx.dagger.hilt

import com.bumble.appyx.dagger.hilt.generator.aggregate.AggregateNodeFactoryModuleGenerator
import com.bumble.appyx.dagger.hilt.generator.custom.CustomNodeFactoryModuleGenerator
import com.bumble.appyx.dagger.hilt.generator.ModuleGenerator
import com.bumble.appyx.dagger.hilt.generator.factory.NodeFactoryModuleGenerator
import com.bumble.appyx.dagger.hilt.generator.node.NodeModuleGenerator
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal class AppyxHiltProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        generate(
            resolver = resolver,
            annotation = HiltCustomNodeFactory::class.java,
            generator = CustomNodeFactoryModuleGenerator
        )
        generate(
            resolver = resolver,
            annotation = HiltNode::class.java,
            generator = NodeModuleGenerator
        )
        generate(
            resolver = resolver,
            annotation = HiltNodeFactory::class.java,
            generator = NodeFactoryModuleGenerator
        )
        generate(
            resolver = resolver,
            annotation = HiltAggregateNodeFactory::class.java,
            generator = AggregateNodeFactoryModuleGenerator(resolver)
        )

        return emptyList()
    }

    private fun generate(resolver: Resolver, annotation: Class<*>, generator: ModuleGenerator) {
        resolver
            .getSymbolsWithAnnotation(annotation.name)
            .toList()
            .filterIsInstance<KSClassDeclaration>()
            .forEach { declaration -> generator.generateHiltModule(codeGenerator, declaration) }
    }
}

package com.bumble.appyx.dagger.hilt

import com.tschuchort.compiletesting.SourceFile

/**
 * As the core appyx library is an Android library, we must define our own versions of these files.
 * These could easily get out of date, so we will need integration tests elsewhere to handle this possibility.
 */
val buildContextSourceFile = SourceFile.kotlin(
    "BuildContext.kt",
    """
        package com.bumble.appyx.core.modality
    
        interface BuildContext
    """.trimIndent()
)

val nodeSourceFile = SourceFile.kotlin(
    "Node.kt",
    """
        package com.bumble.appyx.core.node
        
        import com.bumble.appyx.core.modality
        
        class Node(buildContext: BuildContext)
    """.trimIndent()
)

val nodeFactorySourceFile = SourceFile.kotlin(
    "BuildContext.kt",
    """
        package com.bumble.appyx.core.integration
    
        fun interface NodeFactory<N : Node> {
            fun create(buildContext: BuildContext): N
        }
    """.trimIndent()
)

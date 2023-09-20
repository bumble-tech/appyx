package com.bumble.appyx

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension

val Project.versionCatalog
    get() = extensions
        .getByType(VersionCatalogsExtension::class.java)
        .named("libs")

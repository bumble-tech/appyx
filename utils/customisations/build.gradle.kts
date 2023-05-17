plugins {
    kotlin("multiplatform")
    id("appyx-publish-multiplatform")
    id("appyx-detekt")
}

publishingPlugin {
    artifactId = "utils-customisations"
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
        }
    }
    js(IR) {
        // Adding moduleName as a workaround for this issue: https://youtrack.jetbrains.com/issue/KT-51942
        moduleName = "appyx-utils-customisation"
        browser()
    }
    sourceSets {
        val commonMain by getting
    }
}

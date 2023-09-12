plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.detekt)
}

dependencies {
    implementation(libs.plugin.android)
    implementation(libs.plugin.detekt)
}

detekt {
    buildUponDefaultConfig = true
    config.from(file("../../detekt.yml"))
}

gradlePlugin {
    plugins {
        create("appyx-collect-sarif") {
            id = "appyx-collect-sarif"
            implementationClass = "CollectSarifPlugin"
        }
        create("appyx-lint") {
            id = "appyx-lint"
            implementationClass = "LintPlugin"
        }
        create("appyx-detekt") {
            id = "appyx-detekt"
            implementationClass = "DetektPlugin"
        }
    }
}

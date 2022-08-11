import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.plugin)
}

tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile::class.java).configureEach {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.name
}

gradlePlugin {
    plugins {
        create("appyx-collect-lint-sarif") {
            id = "appyx-collect-lint-sarif"
            implementationClass = "CollectLintSarifPlugin"
        }
        create("appyx-report-lint-sarif") {
            id = "appyx-report-lint-sarif"
            implementationClass = "ReportLintSarifPlugin"
        }
    }
}

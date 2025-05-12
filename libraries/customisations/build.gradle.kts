import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("appyx-publish")
    id("appyx-detekt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType(KotlinJvmCompile::class.java).configureEach {
    kotlinOptions.jvmTarget = JvmTarget.JVM_11.toString()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

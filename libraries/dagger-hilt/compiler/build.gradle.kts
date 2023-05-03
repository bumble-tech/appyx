plugins {
    id("java-library")
    id("kotlin")
    kotlin("kapt")
    id("appyx-publish-java")
    id("appyx-detekt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":libraries:dagger-hilt:common"))
    implementation(libs.kotlinpoet.core)
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.ksp.processor)
    implementation(libs.dagger.hilt.core)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.ksp.testing)
}

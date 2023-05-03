plugins {
    id("kotlin")
    id("appyx-publish-java")
    id("appyx-detekt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(libs.dagger.hilt.core)
}

plugins {
    id("java-library")
    id("kotlin")
    id("appyx-publish-java")
    id("appyx-detekt")
}

publishingPlugin {
    artifactId = "utils-customisations"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

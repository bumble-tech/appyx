plugins {
    id("com.bumble.appyx.multiplatform")
}

kotlin {
    js(IR) {
        moduleName = "demos-mkdocs-common"
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
            }
        }
    }
}

compose.experimental {
    web.application {}
}

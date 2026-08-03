plugins {
    `kotlin-dsl`
}

group = "com.androidtechmix.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.hilt.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "androidtechmix.android.application"
            implementationClass = "com.androidtechmix.convention.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "androidtechmix.android.library"
            implementationClass = "com.androidtechmix.convention.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "androidtechmix.android.compose"
            implementationClass = "com.androidtechmix.convention.AndroidComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "androidtechmix.android.feature"
            implementationClass = "com.androidtechmix.convention.AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "androidtechmix.android.hilt"
            implementationClass = "com.androidtechmix.convention.AndroidHiltConventionPlugin"
        }
        register("jvmLibrary") {
            id = "androidtechmix.jvm.library"
            implementationClass = "com.androidtechmix.convention.JvmLibraryConventionPlugin"
        }
    }
}

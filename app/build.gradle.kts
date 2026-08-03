import java.util.Properties

plugins {
    alias(libs.plugins.androidtechmix.android.application)
    alias(libs.plugins.androidtechmix.android.compose)
    alias(libs.plugins.androidtechmix.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.androidtechmix.githubusers"

    defaultConfig {
        applicationId = "com.androidtechmix.githubusers"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }
        val githubToken = (localProperties.getProperty("github.token") ?: "")
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
        buildConfigField("String", "GITHUB_TOKEN", "\"$githubToken\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(projects.feature.search)
    implementation(projects.feature.favorites)
    implementation(projects.feature.userdetail)
    implementation(projects.core.data)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.core.common)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.truth)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

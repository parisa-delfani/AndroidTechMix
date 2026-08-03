plugins {
    alias(libs.plugins.androidtechmix.android.library)
    alias(libs.plugins.androidtechmix.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.androidtechmix.githubusers.core.network"
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.kotlinx.coroutines.test)
}

plugins {
    alias(libs.plugins.androidtechmix.android.library)
    alias(libs.plugins.androidtechmix.android.hilt)
}

android {
    namespace = "com.androidtechmix.githubusers.core.data"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    api(projects.core.network)
    api(projects.core.database)

    implementation(libs.paging.runtime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)

    testImplementation(projects.core.testing)
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.room.testing)
    testImplementation(libs.retrofit)
    testImplementation(libs.retrofit.kotlinx.serialization)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.okhttp)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
}

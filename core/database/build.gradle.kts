plugins {
    alias(libs.plugins.androidtechmix.android.library)
    alias(libs.plugins.androidtechmix.android.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.androidtechmix.githubusers.core.database"
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    implementation(libs.paging.runtime)
    ksp(libs.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.room.testing)
    testImplementation(libs.kotlinx.coroutines.test)
}

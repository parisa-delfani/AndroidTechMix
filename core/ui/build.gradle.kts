plugins {
    alias(libs.plugins.androidtechmix.android.library)
    alias(libs.plugins.androidtechmix.android.compose)
}

android {
    namespace = "com.androidtechmix.githubusers.core.ui"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(libs.coil.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
}

plugins {
    alias(libs.plugins.androidtechmix.android.feature)
}

android {
    namespace = "com.androidtechmix.githubusers.feature.search"
}

dependencies {
    implementation(libs.paging.runtime)
}

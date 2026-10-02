plugins {
    alias(libs.plugins.androidtechmix.android.feature)
}

android {
    namespace = "com.androidtechmix.githubusers.feature.compare"

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    testImplementation(libs.robolectric)
}

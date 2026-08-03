plugins {
    alias(libs.plugins.androidtechmix.jvm.library)
}

dependencies {
    api(projects.core.common)
    api(libs.paging.common)
    implementation(libs.javax.inject)
}

plugins {
    alias(libs.plugins.androidtechmix.jvm.library)
}

dependencies {
    api(projects.core.common)
    api(projects.core.domain)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
    api(libs.truth)
    api(libs.junit)
}

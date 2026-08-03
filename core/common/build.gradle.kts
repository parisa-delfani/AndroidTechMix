plugins {
    alias(libs.plugins.androidtechmix.jvm.library)
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

}

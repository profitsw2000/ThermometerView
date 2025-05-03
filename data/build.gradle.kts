plugins {
    alias(libs.plugins.tv.main.lib.gradle.plugin)
}

android {
    namespace = "ru.profitsw2000.data"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
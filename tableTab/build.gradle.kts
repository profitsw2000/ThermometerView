plugins {
    alias(libs.plugins.tv.main.lib.gradle.plugin)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.profitsw2000.tabletab"
}

dependencies {

    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":navigator"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
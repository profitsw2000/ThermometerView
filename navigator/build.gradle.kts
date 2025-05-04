plugins {
    alias(libs.plugins.tv.main.lib.gradle.plugin)
}

android {
    namespace = "ru.profitsw2000.navigator"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
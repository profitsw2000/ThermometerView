plugins {
    alias(libs.plugins.tv.main.lib.gradle.plugin)
}

android {
    namespace = "ru.profitsw2000.data"
}

dependencies {

    implementation(project(":core"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    //Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.androidx.room.compiler)
    //Paging
    implementation(libs.androidx.paging)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
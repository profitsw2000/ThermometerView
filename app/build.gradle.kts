plugins {
    alias(libs.plugins.tv.main.app.gradle.plugin)
}

android {
    namespace = "ru.profitsw2000.thermometerview"
}

dependencies {
    //Modules
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":mainFragment"))
    implementation(project(":memoryFragment"))
    implementation(project(":tableTab"))
    implementation(project(":graphTab"))
    implementation(project(":navigator"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    //Koin
    implementation(libs.koin)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
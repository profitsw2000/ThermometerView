plugins {
    alias(libs.plugins.tv.main.lib.gradle.plugin)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.profitsw2000.mainfragment"
}

dependencies {

    implementation(project(":core"))
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    //ViewModel
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.livedata)
    implementation(libs.androidx.viewmodel)
    //Koin
    implementation(libs.koin)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
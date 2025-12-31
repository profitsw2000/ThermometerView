plugins {
    alias(libs.plugins.tv.main.lib.gradle.plugin)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.profitsw2000.graphtab"
}

dependencies {

    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":navigator"))

    implementation(libs.androidx.fragment)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    //ViewModel
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.livedata)
    implementation(libs.androidx.viewmodel)
    //Koin
    implementation(libs.koin)
    //Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.androidx.room.compiler)

    implementation(libs.mp.android.chart)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
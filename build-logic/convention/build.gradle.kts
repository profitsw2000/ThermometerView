plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("mainApplicationGradlePlugin") {
            id = libs.plugins.tv.main.app.gradle.plugin.get().pluginId
            implementationClass = "MainApplicationGradlePlugin"
        }
        register("mainLibraryGradlePlugin") {
            id = libs.plugins.tv.main.lib.gradle.plugin.get().pluginId
            implementationClass = "MainLibraryGradlePlugin"
        }
    }
}
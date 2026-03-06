plugins {
    alias(libs.plugins.tv.main.lib.gradle.plugin)
}

android {
    namespace = "ru.profitsw2000.data"

/*    testOptions {
        unitTests.all {
            // Это заставит Gradle чаще пересоздавать процесс воркера,
            // что очищает проблемные пути в памяти
            forkEvery = 1
            maxParallelForks = 1
        }
    }*/
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

    tasks.withType<Test> {
        // После завершения тестов выводим ссылку на отчет прямо в консоль
        afterSuite(KotlinClosure2<TestDescriptor, TestResult, Unit>({ descriptor, result ->
            if (descriptor.parent == null) { // Это корневой набор тестов
                val reportUrl = reports.html.outputLocation.get().asFile.toURI()
                println("\n--- ТЕСТЫ ЗАВЕРШЕНЫ ---")
                println("Результат: ${result.resultType}")
                println("Всего тестов: ${result.testCount} (Успешно: ${result.successfulTestCount}, Провалено: ${result.failedTestCount})")
                println("ОТЧЕТ ТУТ: $reportUrl")
                println("-----------------------\n")
            }
        }))
    }
}
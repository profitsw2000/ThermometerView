package ru.profitsw2000.thermometerview

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.profitsw2000.mainfragment.di.mainModule
import ru.profitsw2000.memoryfragment.di.memoryModule
import ru.profitsw2000.tabletab.di.tableModule
import ru.profitsw2000.thermometerview.di.appModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                appModule,
                mainModule,
                memoryModule,
                tableModule
            )
        }
    }
}
package ru.profitsw2000.thermometerview.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.profitsw2000.data.room.database.AppDatabase

val appModule = module {
    single {
        AppDatabase.create(androidContext())
        AppDatabase.getInstance()
    }
}
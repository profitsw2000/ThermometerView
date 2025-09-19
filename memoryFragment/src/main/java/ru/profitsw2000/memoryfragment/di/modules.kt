package ru.profitsw2000.memoryfragment.di

import org.koin.dsl.module
import ru.profitsw2000.memoryfragment.presentation.viewmodel.MemoryViewModel

val memoryModule = module{
    single { MemoryViewModel(get(), get()) }
}
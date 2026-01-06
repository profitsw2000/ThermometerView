package ru.profitsw2000.graphtab.di

import org.koin.dsl.module
import ru.profitsw2000.graphtab.presentation.viewmodel.GraphViewModel

val graphModule = module{
    single<GraphViewModel> { GraphViewModel(get(), get()) }
}
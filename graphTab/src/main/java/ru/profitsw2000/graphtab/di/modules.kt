package ru.profitsw2000.graphtab.di

import org.koin.dsl.module
import ru.profitsw2000.data.data.filter.SensorHistoryGraphFilterRepositoryImpl
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.graphtab.presentation.viewmodel.GraphViewModel

val graphModule = module{
    single<GraphViewModel> { GraphViewModel(get(), get(), get()) }
    single<SensorHistoryGraphFilterRepository> { SensorHistoryGraphFilterRepositoryImpl() }
}
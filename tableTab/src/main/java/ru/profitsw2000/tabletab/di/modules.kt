package ru.profitsw2000.tabletab.di

import org.koin.dsl.module
import ru.profitsw2000.data.data.local.source.HistoryListPagingSource
import ru.profitsw2000.tabletab.presentation.viewmodel.FilterViewModel
import ru.profitsw2000.tabletab.presentation.viewmodel.TableViewModel

val tableModule = module {
    single<TableViewModel> { TableViewModel(get()) }
    single<FilterViewModel> { FilterViewModel(get()) }
}
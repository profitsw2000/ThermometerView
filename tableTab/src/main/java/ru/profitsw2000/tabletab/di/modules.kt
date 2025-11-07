package ru.profitsw2000.tabletab.di

import org.koin.dsl.module
import ru.profitsw2000.data.data.local.source.HistoryListPagingSource
import ru.profitsw2000.tabletab.presentation.viewmodel.TableViewModel

val tableModule = module {
    single<HistoryListPagingSource> { HistoryListPagingSource(get(), get()) }
    single<TableViewModel> { TableViewModel(get()) }
}
package ru.profitsw2000.memoryfragment.di

import org.koin.dsl.module
import ru.profitsw2000.data.data.local.SensorHistoryRepositoryLocalImpl
import ru.profitsw2000.data.data.remote.SensorHistoryRepositoryRemoteImpl
import ru.profitsw2000.data.domain.local.SensorHistoryRepositoryLocal
import ru.profitsw2000.data.domain.remote.SensorHistoryRepositoryRemote
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.memoryfragment.presentation.viewmodel.MemoryViewModel

val memoryModule = module{
    single { MemoryViewModel(get(), get(), get(), get()) }
    single<SensorHistoryRepositoryLocal> { SensorHistoryRepositoryLocalImpl(get()) }
    single<SensorHistoryRepositoryRemote> { SensorHistoryRepositoryRemoteImpl() }
    factory { SensorHistoryInteractor(get(), get()) }
    factory { SensorHistoryMapper() }
}
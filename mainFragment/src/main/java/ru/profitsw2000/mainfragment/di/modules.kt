package ru.profitsw2000.mainfragment.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.profitsw2000.data.data.BluetoothPacketManagerImpl
import ru.profitsw2000.data.data.BluetoothRepositoryImpl
import ru.profitsw2000.data.data.DateTimeRepositoryImpl
import ru.profitsw2000.data.domain.BluetoothPacketManager
import ru.profitsw2000.data.domain.BluetoothRepository
import ru.profitsw2000.data.domain.DateTimeRepository
import ru.profitsw2000.mainfragment.presentation.viewmodel.MainViewModel
import ru.profitsw2000.mainfragment.presentation.viewmodel.PairedDevicesViewModel
import ru.profitsw2000.mainfragment.presentation.viewmodel.SensorInfoViewModel

val mainModule = module {
    single<BluetoothRepository> { BluetoothRepositoryImpl(androidContext()) }
    single<DateTimeRepository> { DateTimeRepositoryImpl() }
    single<BluetoothPacketManager> { BluetoothPacketManagerImpl(get()) }
    single { MainViewModel(get(), get(), get()) }
    single { PairedDevicesViewModel(get()) }
    single { SensorInfoViewModel(get(), get()) }
}
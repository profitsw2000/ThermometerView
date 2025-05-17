package ru.profitsw2000.mainfragment.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.profitsw2000.data.data.BluetoothRepositoryImpl
import ru.profitsw2000.data.domain.BluetoothRepository
import ru.profitsw2000.mainfragment.presentation.viewmodel.MainViewModel
import ru.profitsw2000.mainfragment.presentation.viewmodel.PairedDevicesViewModel

val mainModule = module {
    single<BluetoothRepository> { BluetoothRepositoryImpl(androidContext()) }
    single { MainViewModel(get()) }
    single { PairedDevicesViewModel(get()) }
}
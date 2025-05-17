package ru.profitsw2000.mainfragment.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.profitsw2000.data.domain.BluetoothRepository

class PairedDevicesViewModel(
    private val bluetoothRepository: BluetoothRepository
): ViewModel() {
    private val bluetoothIsEnabledData: LiveData<Boolean> = bluetoothRepository.bluetoothIsEnabledData.asLiveData()
    val pairedDevicesStringList: LiveData<List<String>> = bluetoothRepository.bluetoothPairedDevicesStringList.asLiveData()

    fun getPairedDevicesStringList() {
        if (bluetoothIsEnabledData.value == true) {
            val pairedDevicesList = bluetoothRepository.getPairedDevicesStringList()
        }
    }
}
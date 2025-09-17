package ru.profitsw2000.memoryfragment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import ru.profitsw2000.data.domain.BluetoothPacketManager
import ru.profitsw2000.data.domain.BluetoothRepository

class MemoryViewModel(
    private val bluetoothRepository: BluetoothRepository,
    private val bluetoothPacketManager: BluetoothPacketManager
) : ViewModel() {



}
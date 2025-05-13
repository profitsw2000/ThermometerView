package ru.profitsw2000.data.domain

import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {
    val bluetoothIsEnabledData: StateFlow<Boolean>
    //val bluetoothStateBroadcastReceiver: BluetoothStateBroadcastReceiver
    val bluetoothPairedDevicesStringList: StateFlow<List<String>>

    fun initBluetooth()

    fun registerReceiver()

    fun unregisterReceiver()

    fun disableBluetooth()
}
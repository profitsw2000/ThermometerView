package ru.profitsw2000.data.model

sealed class BluetoothConnectionStatus {
    data object Disconnected: BluetoothConnectionStatus()
    data object DeviceSelection: BluetoothConnectionStatus()
    data object Connecting: BluetoothConnectionStatus()
    data object Connected: BluetoothConnectionStatus()
    data object Failed: BluetoothConnectionStatus()
}
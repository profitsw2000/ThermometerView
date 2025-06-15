package ru.profitsw2000.data.model.status

sealed class BluetoothConnectionStatus {
    data object Disconnected: BluetoothConnectionStatus()
    data object DeviceSelection: BluetoothConnectionStatus()
    data object Connecting: BluetoothConnectionStatus()
    data object Connected: BluetoothConnectionStatus()
    data object Failed: BluetoothConnectionStatus()
}
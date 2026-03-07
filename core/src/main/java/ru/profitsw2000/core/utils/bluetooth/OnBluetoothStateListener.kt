package ru.profitsw2000.core.utils.bluetooth

interface OnBluetoothStateListener {
    fun onBluetoothStateChanged(bluetoothIsEnabled: Boolean)
}
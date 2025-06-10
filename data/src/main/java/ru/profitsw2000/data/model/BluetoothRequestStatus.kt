package ru.profitsw2000.data.model

sealed class BluetoothRequestStatus {
    data class onSuccess(val byteArray: ByteArray) : BluetoothRequestStatus()
    data object onError : BluetoothRequestStatus()
}
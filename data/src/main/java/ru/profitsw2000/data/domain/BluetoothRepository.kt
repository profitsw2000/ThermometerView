package ru.profitsw2000.data.domain

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.StateFlow
import ru.profitsw2000.core.utils.bluetooth.BluetoothStateBroadcastReceiver
import ru.profitsw2000.data.model.status.BluetoothConnectionStatus

interface BluetoothRepository {
    val bluetoothIsEnabledData: StateFlow<Boolean>
    val bluetoothStateBroadcastReceiver: BluetoothStateBroadcastReceiver
    val bluetoothPairedDevicesStringList: StateFlow<List<String>>
    val bluetoothReadByteList: StateFlow<List<Byte>>
    val isDeviceConnected: Boolean

    fun initBluetooth()

    fun getPairedDevicesStringList(): List<BluetoothDevice>

    suspend fun connectDevice(device: BluetoothDevice): BluetoothConnectionStatus

    suspend fun disconnectDevice(): BluetoothConnectionStatus

    suspend fun writeByteArray(byteArray: ByteArray): Boolean

    fun readByteArray()

    fun registerReceiver()

    fun unregisterReceiver()

    fun disableBluetooth()
}
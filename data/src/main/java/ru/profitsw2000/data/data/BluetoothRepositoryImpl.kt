package ru.profitsw2000.data.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.profitsw2000.data.domain.BluetoothRepository

class BluetoothRepositoryImpl(
    private val context: Context
) : BluetoothRepository {

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        bluetoothManager.adapter
    }
    private val mutableBluetoothEnabledData = MutableStateFlow(false)
    override val bluetoothIsEnabledData: StateFlow<Boolean>
        get() = mutableBluetoothEnabledData
    private val bluetoothPairedDevicesMutableStringList = MutableStateFlow<List<String>>(listOf())
    override val bluetoothPairedDevicesStringList: StateFlow<List<String>>
        get() = bluetoothPairedDevicesMutableStringList

    override fun initBluetooth() {
        mutableBluetoothEnabledData.value = bluetoothAdapter.isEnabled
    }

    override fun registerReceiver() {
        TODO("Not yet implemented")
    }

    override fun unregisterReceiver() {
        TODO("Not yet implemented")
    }

    @SuppressLint("MissingPermission")
    override fun disableBluetooth() {
        bluetoothAdapter.disable()
        bluetoothPairedDevicesMutableStringList.value = listOf()
    }
}
package ru.profitsw2000.data.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.profitsw2000.core.utils.bluetooth.BluetoothStateBroadcastReceiver
import ru.profitsw2000.core.utils.bluetooth.OnBluetoothStateListener
import ru.profitsw2000.data.domain.BluetoothRepository
import ru.profitsw2000.data.model.status.BluetoothConnectionStatus
import java.io.IOException
import java.io.InputStream
import java.util.UUID

class BluetoothRepositoryImpl(
    private val context: Context
) : BluetoothRepository, OnBluetoothStateListener {
    private val TAG = "VVV"

    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isDeviceConnected = false

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        bluetoothManager.adapter
    }
    override val bluetoothStateBroadcastReceiver = BluetoothStateBroadcastReceiver(this)

    private val mutableBluetoothEnabledData = MutableStateFlow(false)
    override val bluetoothIsEnabledData: StateFlow<Boolean>
        get() = mutableBluetoothEnabledData
    private val bluetoothPairedDevicesMutableStringList = MutableStateFlow<List<String>>(listOf())
    override val bluetoothPairedDevicesStringList: StateFlow<List<String>>
        get() = bluetoothPairedDevicesMutableStringList
    private val bluetoothReadByteMutableList = MutableStateFlow<List<Byte>>(listOf())
    override val bluetoothReadByteList: StateFlow<List<Byte>>
        get() = bluetoothReadByteMutableList


    override fun initBluetooth() {
        mutableBluetoothEnabledData.value = bluetoothAdapter.isEnabled
    }

    @SuppressLint("MissingPermission")
    override fun getPairedDevicesStringList(): List<BluetoothDevice> {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        val pairedDevicesNameList = arrayListOf<String>()
        val pairedDevicesList = arrayListOf<BluetoothDevice>()
        pairedDevices?.forEach { device ->
            pairedDevicesNameList.add(device.name)
            pairedDevicesList.add(device)
        }
        bluetoothPairedDevicesMutableStringList.value = pairedDevicesNameList
        return pairedDevicesList
    }

    @SuppressLint("MissingPermission")
    override suspend fun connectDevice(device: BluetoothDevice): BluetoothConnectionStatus {
        bluetoothDevice = device
        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)

        val deferred: Deferred<BluetoothConnectionStatus> = coroutineScope.async {
            try {
                bluetoothSocket.let {
                    bluetoothSocket.connect()
                }
                isDeviceConnected = true
                readByteArray()
                BluetoothConnectionStatus.Connected
            } catch (ioException: IOException) {
                return@async BluetoothConnectionStatus.Failed
            }
        }
        return deferred.await()
    }

    override suspend fun disconnectDevice(): BluetoothConnectionStatus {
        val deferred: Deferred<BluetoothConnectionStatus> = coroutineScope.async {
            try {
                isDeviceConnected = false
                bluetoothSocket.let {
                    bluetoothSocket.close()
                }
                BluetoothConnectionStatus.Disconnected
            } catch (ioException: IOException) {
                return@async BluetoothConnectionStatus.Connected
            }
        }
        return deferred.await()
    }

    override suspend fun writeByteArray(byteArray: ByteArray): Boolean {
        return if (bluetoothSocket.isConnected) {
            val outputStream = bluetoothSocket.outputStream
            val deferred: Deferred<Boolean> = coroutineScope.async {
                try {
                    outputStream.write(byteArray)
                    true
                } catch (exception: Exception) {
                    false
                }
            }
            deferred.await()
        } else false
    }

    override fun readByteArray() {
        val inputStream: InputStream = bluetoothSocket.inputStream
        val byteArray = ByteArray(1024)

        coroutineScope.launch {
            while (isDeviceConnected) {
                val bytesNumber: Int = try {
                    inputStream.read(byteArray)
                } catch (ioException: IOException) {
                    Log.d(TAG, "ioException: IOException")
                    break
                }
                    //inputStream.read(byteArray)
                bluetoothReadByteMutableList.value = byteArrayToList(byteArray, bytesNumber)
/*                try {
                    inputStream.read(byteArray)
                    Log.d(TAG, "readByteArray: ${byteArray.toHex()}")
                } catch (ioException: IOException) {
                    Log.d(TAG, "ioException: IOException")
                    break
                }*/
                Log.d(TAG, "bytesNumber: $bytesNumber")
                Log.d(TAG, "readByteArray: ${byteArray.toHex()}")
                Log.d(TAG, "readByteArray: ${bluetoothReadByteList.value}")
            }
        }
    }

    override fun registerReceiver() {
        context.registerReceiver(bluetoothStateBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun unregisterReceiver() {
        context.unregisterReceiver(bluetoothStateBroadcastReceiver)
    }

    @SuppressLint("MissingPermission")
    override fun disableBluetooth() {
        bluetoothAdapter.disable()
        bluetoothPairedDevicesMutableStringList.value = listOf()
    }

    override fun onBluetoothStateChanged(bluetoothIsEnabled: Boolean) {
        mutableBluetoothEnabledData.value = bluetoothIsEnabled
    }

    private fun byteArrayToList(byteArray: ByteArray, size: Int): List<Byte> {
        val mutableList: MutableList<Byte> = mutableListOf()
        for(i in 0..<size) {
            mutableList.add(byteArray[i])
        }
        return mutableList
    }

    private fun ByteArray.toHex(): String = joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }
}
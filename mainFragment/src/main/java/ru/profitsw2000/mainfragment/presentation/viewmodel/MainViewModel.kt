package ru.profitsw2000.mainfragment.presentation.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.os.Build.VERSION
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.profitsw2000.core.utils.constants.mainDataBluetoothRequestsList
import ru.profitsw2000.data.domain.BluetoothRepository
import ru.profitsw2000.data.domain.DateTimeRepository
import ru.profitsw2000.data.model.BluetoothConnectionStatus
import ru.profitsw2000.data.model.BluetoothRequestStatus

class MainViewModel(
    private val bluetoothRepository: BluetoothRepository,
    private val dateTimeRepository: DateTimeRepository
) : ViewModel(), DefaultLifecycleObserver {

    var bluetoothRequestId = 0
    private var isRequestInProgress = false
    val dateTimeLiveData: LiveData<String> = dateTimeRepository.dateTimeDataString.asLiveData()
    val dataExchangeStartSignalData: LiveData<Boolean> = dateTimeRepository.dataExchangeStartSignal.asLiveData()

    val bluetoothIsEnabledData: LiveData<Boolean> = bluetoothRepository.bluetoothIsEnabledData.asLiveData()
    private lateinit var pairedDevicesList: List<BluetoothDevice>
    private var _bluetoothConnectionStatus: MutableLiveData<BluetoothConnectionStatus> = MutableLiveData(BluetoothConnectionStatus.Disconnected)
    val bluetoothConnectionStatus by this::_bluetoothConnectionStatus

/*    private val bluetoothReadByteArray: LiveData<ByteArray> = bluetoothRepository.bluetoothReadByteArray.asLiveData()
    private val bluetoothSuccessRequestStatus = bluetoothReadByteArray.map {
        bytes: ByteArray -> getBluetoothRequestStatus(bytes)
    }*/
    private var _bluetoothErrorRequestStatus: MutableLiveData<BluetoothRequestStatus> = MutableLiveData(BluetoothRequestStatus.onError)
    val bluetoothErrorRequestStatus by this::_bluetoothErrorRequestStatus

    private var bluetoothRequestStatus = MediatorLiveData<BluetoothRequestStatus>()

    private fun getBluetoothRequestStatus(byteArray: ByteArray): BluetoothRequestStatus {
        isRequestInProgress = false
        return BluetoothRequestStatus.onSuccess(byteArray)
    }

    fun initBluetooth(permissionIsGranted: Boolean) {
        if (permissionIsGranted) bluetoothRepository.initBluetooth()
/*        bluetoothRequestStatus.addSource(bluetoothSuccessRequestStatus) { value ->
            bluetoothRequestStatus.value = value
        }*/
        bluetoothRequestStatus.addSource(bluetoothErrorRequestStatus) { value ->
            bluetoothRequestStatus.value = value
        }
    }

    fun deviceConnection() {
        if (bluetoothIsEnabledData.value == true) {
            when(bluetoothConnectionStatus.value) {
                BluetoothConnectionStatus.Disconnected -> bluetoothConnectionStatus.value = BluetoothConnectionStatus.DeviceSelection
                BluetoothConnectionStatus.Connected -> disconnectDevice()
                BluetoothConnectionStatus.Failed -> bluetoothConnectionStatus.value = BluetoothConnectionStatus.DeviceSelection
                else -> {}
            }
        } else BluetoothConnectionStatus.Disconnected
    }

    fun setCurrentState(bluetoothConnectionStatus: BluetoothConnectionStatus) {
        _bluetoothConnectionStatus.value = bluetoothConnectionStatus
    }

    fun connectSelectedDevice(index: Int) {
        _bluetoothConnectionStatus.value = BluetoothConnectionStatus.Connecting
        pairedDevicesList = bluetoothRepository.getPairedDevicesStringList()
        pairedDevicesList[index].let {
            val device = pairedDevicesList[index]
            viewModelScope.launch {
                _bluetoothConnectionStatus.value = bluetoothRepository.connectDevice(device)
            }
        }
    }

    private fun disconnectDevice() {
        viewModelScope.launch {
            _bluetoothConnectionStatus.value = bluetoothRepository.disconnectDevice()
        }
    }

    fun getPairedDevicesStringList() {
        if (bluetoothIsEnabledData.value == true) {
            pairedDevicesList = bluetoothRepository.getPairedDevicesStringList()
        }
    }

    fun sendRequest(byteArray: ByteArray) {
        if (bluetoothConnectionStatus.value == BluetoothConnectionStatus.Connected) {
            viewModelScope.launch {
                bluetoothRepository.writeByteArray(byteArray)
                Log.d("VVV", "sendRequest: ${byteArray.toHex()}")
            }
        }
    }

    fun requestMainScreenData() {
        val requestByteArray = mainDataBluetoothRequestsList[bluetoothRequestId]
        bluetoothRequestId++
        bluetoothRequestId %= 3
        sendRequest(requestByteArray)
    }

    @SuppressLint("SuspiciousIndentation")
    fun disableBluetooth() {
        if (VERSION.SDK_INT <= Build.VERSION_CODES.S){
            if (bluetoothConnectionStatus.value == BluetoothConnectionStatus.Disconnected ||
                bluetoothConnectionStatus.value == BluetoothConnectionStatus.Failed)
                bluetoothRepository.disableBluetooth()
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        bluetoothRepository.registerReceiver()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        bluetoothRepository.unregisterReceiver()
    }

    fun ByteArray.toHex(): String = joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }
}
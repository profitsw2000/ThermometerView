package ru.profitsw2000.mainfragment.presentation.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.os.Build.VERSION
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.profitsw2000.data.domain.BluetoothRepository
import ru.profitsw2000.data.model.BluetoothConnectionStatus

class MainViewModel(
    private val bluetoothRepository: BluetoothRepository
) : ViewModel(), DefaultLifecycleObserver {

    val bluetoothIsEnabledData: LiveData<Boolean> = bluetoothRepository.bluetoothIsEnabledData.asLiveData()
    private lateinit var pairedDevicesList: List<BluetoothDevice>
    private var _bluetoothConnectionStatus: MutableLiveData<BluetoothConnectionStatus> = MutableLiveData(BluetoothConnectionStatus.Disconnected)
    val bluetoothConnectionStatus by this::_bluetoothConnectionStatus

    fun initBluetooth(permissionIsGranted: Boolean) {
        if (permissionIsGranted) bluetoothRepository.initBluetooth()
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
}
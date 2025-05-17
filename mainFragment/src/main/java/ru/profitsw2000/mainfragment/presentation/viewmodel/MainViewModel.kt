package ru.profitsw2000.mainfragment.presentation.viewmodel

import android.annotation.SuppressLint
import android.os.Build
import android.os.Build.VERSION
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.profitsw2000.data.domain.BluetoothRepository

class MainViewModel(
    private val bluetoothRepository: BluetoothRepository
) : ViewModel(), DefaultLifecycleObserver {

    val bluetoothIsEnabledData: LiveData<Boolean> = bluetoothRepository.bluetoothIsEnabledData.asLiveData()

    fun initBluetooth(permissionIsGranted: Boolean) {
        if (permissionIsGranted) bluetoothRepository.initBluetooth()
    }

    @SuppressLint("SuspiciousIndentation")
    fun disableBluetooth() {
        if (VERSION.SDK_INT <= Build.VERSION_CODES.S){
/*            if (bluetoothConnectionStatus.value == BluetoothConnectionStatus.Disconnected ||
                bluetoothConnectionStatus.value == BluetoothConnectionStatus.Failed)*/
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
package ru.profitsw2000.mainfragment.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.profitsw2000.data.domain.BluetoothRepository
import ru.profitsw2000.data.model.state.SensorInfoState

class SensorInfoViewModel(
    private val bluetoothRepository: BluetoothRepository
) : ViewModel() {

    private var _sensorInfoLiveData: MutableLiveData<SensorInfoState> = MutableLiveData(SensorInfoState.Loading)
    val sensorInfoLiveData by this::_sensorInfoLiveData
    private val lifecycleScope = CoroutineScope(Dispatchers.Main)

    fun sendSensorInfoRequest(index: Int) {

    }
}
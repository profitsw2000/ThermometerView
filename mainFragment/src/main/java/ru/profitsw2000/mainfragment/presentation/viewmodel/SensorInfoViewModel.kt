package ru.profitsw2000.mainfragment.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.profitsw2000.core.utils.constants.DATA_EXCHANGE_INTERVAL
import ru.profitsw2000.core.utils.constants.SENSOR_INFO_REQUEST_INTERVAL
import ru.profitsw2000.core.utils.constants.getSensorInfoPacket
import ru.profitsw2000.core.utils.constants.getSensorLetterCodePacket
import ru.profitsw2000.data.domain.BluetoothPacketManager
import ru.profitsw2000.data.domain.BluetoothRepository
import ru.profitsw2000.data.model.state.SensorInfoState
import ru.profitsw2000.data.model.status.BluetoothRequestResultStatus

class SensorInfoViewModel(
    private val bluetoothRepository: BluetoothRepository,
    private val bluetoothPacketManager: BluetoothPacketManager
) : ViewModel() {

    private var writeBufferIsBusy = false
    var sensorInfoLiveData = MediatorLiveData<SensorInfoState>()
    private var _sensorInfoRequestLiveData: MutableLiveData<SensorInfoState> = MutableLiveData(SensorInfoState.Loading)
    private val sensorInfoRequestLiveData by this::_sensorInfoRequestLiveData
    private val bluetoothRequestResult: LiveData<BluetoothRequestResultStatus> = bluetoothPacketManager.bluetoothRequestResult.asLiveData()
    private val sensorInfoResultLiveData = bluetoothRequestResult.map { status: BluetoothRequestResultStatus ->
        getBluetoothReceivedDataRequestStatus(status)
    }
    private val lifecycleScope = CoroutineScope(Dispatchers.Main)

    init {
        sensorInfoLiveData.addSource(sensorInfoRequestLiveData) { value ->
            sensorInfoLiveData.value = value
        }
        sensorInfoLiveData.addSource(sensorInfoResultLiveData) { value ->
            sensorInfoLiveData.value = value
        }
    }

    private fun getBluetoothReceivedDataRequestStatus(bluetoothRequestResultStatus: BluetoothRequestResultStatus): SensorInfoState {
        return when(bluetoothRequestResultStatus) {
            is BluetoothRequestResultStatus.SensorInfo -> SensorInfoState.Success(bluetoothRequestResultStatus.sensorModel)
            else -> SensorInfoState.Error
        }
    }

    private suspend fun sendSensorInfoRequest(index: Int) {
        if (bluetoothRepository.isDeviceConnected) {
            _sensorInfoRequestLiveData.value = SensorInfoState.Loading
            writeBufferIsBusy = false
            bluetoothRepository.writeByteArray(getSensorInfoPacket(index))
            writeBufferIsBusy = true
        } else _sensorInfoRequestLiveData.value = SensorInfoState.Error
    }

    private suspend fun sendSensorInfoRequest(index: Int, letter: String) {
        if (bluetoothRepository.isDeviceConnected) {
            writeBufferIsBusy = false
            bluetoothRepository.writeByteArray(getSensorLetterCodePacket(index, letter))
            writeBufferIsBusy = true
        }
    }

    fun startSensorInfoFlow(index: Int) {
        lifecycleScope.launch {
            while (isActive) {
                if (!writeBufferIsBusy) sendSensorInfoRequest(index)
                delay(SENSOR_INFO_REQUEST_INTERVAL)
            }
        }
    }

    fun updateLetter(index: Int, letter: String) {
        lifecycleScope.launch {
            while (isActive) {
                if (!writeBufferIsBusy) sendSensorInfoRequest(index, letter)
            }
        }
    }
}
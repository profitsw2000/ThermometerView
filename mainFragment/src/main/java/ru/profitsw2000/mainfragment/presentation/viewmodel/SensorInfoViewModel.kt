package ru.profitsw2000.mainfragment.presentation.viewmodel

import android.util.Log
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
import ru.profitsw2000.core.utils.constants.TAG
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
    private var sensorInfoIsLoaded = false
    var sensorInfoLiveData = MediatorLiveData<SensorInfoState>()
    private var _sensorInfoRequestLiveData: MutableLiveData<SensorInfoState> = MutableLiveData(SensorInfoState.Loading)
    private val sensorInfoRequestLiveData by this::_sensorInfoRequestLiveData
    private val bluetoothRequestResult: LiveData<BluetoothRequestResultStatus> = bluetoothPacketManager.bluetoothRequestResult.asLiveData()
    private val sensorInfoResultLiveData = bluetoothRequestResult.map { status: BluetoothRequestResultStatus ->
        sensorInfoIsLoaded = true
        getBluetoothReceivedDataRequestStatus(status)
    }
    private lateinit var lifecycleScope: CoroutineScope

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
            setState(SensorInfoState.Loading)
            Log.d(TAG, "sendSensorInfoRequest: Start load data.")
            writeBufferIsBusy = true
            val writeSuccess = bluetoothRepository.writeByteArray(getSensorInfoPacket(index))
            if (!writeSuccess) setState(SensorInfoState.Error)
            writeBufferIsBusy = false
        } else setState(SensorInfoState.Error)
    }

    private suspend fun sendSensorInfoRequest(index: Int, letter: String) {
        if (bluetoothRepository.isDeviceConnected) {
            writeBufferIsBusy = true
            val writeSuccess = bluetoothRepository.writeByteArray(getSensorLetterCodePacket(index, letter))
            if (!writeSuccess) setState(SensorInfoState.Error)
            writeBufferIsBusy = false
        }
    }

    private fun setState(state: SensorInfoState) {
        if (!sensorInfoIsLoaded) _sensorInfoRequestLiveData.value = state
    }

    fun startSensorInfoFlow(index: Int, coroutineScope: CoroutineScope) {
        lifecycleScope = coroutineScope
        lifecycleScope.launch {
            while (isActive) {
                if (!writeBufferIsBusy) {
                    sendSensorInfoRequest(index)
                }
                delay(SENSOR_INFO_REQUEST_INTERVAL)
            }
        }
    }

    fun updateLetter(index: Int, letter: String) {
        lifecycleScope.launch {
            if (!writeBufferIsBusy) sendSensorInfoRequest(index, letter)
        }
    }

    fun ByteArray.toHex(): String = joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }
}
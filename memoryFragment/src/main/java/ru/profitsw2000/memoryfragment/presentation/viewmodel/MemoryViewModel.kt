package ru.profitsw2000.memoryfragment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.profitsw2000.core.utils.constants.MEMORY_DATA_PACKET_TIMEOUT_INTERVAL
import ru.profitsw2000.core.utils.constants.SENSOR_INFO_REQUEST_INTERVAL
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.core.utils.constants.clearMemoryRequestPacket
import ru.profitsw2000.core.utils.constants.currentMemoryAddressRequestPacket
import ru.profitsw2000.core.utils.constants.getSensorInfoPacket
import ru.profitsw2000.data.domain.BluetoothPacketManager
import ru.profitsw2000.data.domain.BluetoothRepository
import ru.profitsw2000.data.model.MemoryServiceDataModel
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.model.state.MemoryScreenState
import ru.profitsw2000.data.model.state.SensorInfoState
import ru.profitsw2000.data.model.status.BluetoothRequestResultStatus

class MemoryViewModel(
    private val bluetoothRepository: BluetoothRepository,
    private val bluetoothPacketManager: BluetoothPacketManager
) : ViewModel() {
    //coroutine
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var lifecycleScope: CoroutineScope
    private val timeIntervalJob = coroutineScope.launch(start = CoroutineStart.LAZY) {
        delay(MEMORY_DATA_PACKET_TIMEOUT_INTERVAL)
        _memoryInfoRequestLiveData.value = MemoryScreenState.TimeoutError
    }

    //memory parameters
    private var currentMemoryAddress: Int = 0
    private var sensorsNum: Int = 0
    private var localIds: List<Int> = arrayListOf()
    private var sensorsLetterCodes: List<Int> = arrayListOf()
    private var sensorIds: List<ULong> = arrayListOf()
    private var sensorHistoryDataModelList: MutableList<SensorHistoryDataModel> = mutableListOf()
    private var memoryAddressCounter = 0

    var memoryInfoLiveData = MediatorLiveData<MemoryScreenState>()
    private var _memoryInfoRequestLiveData: MutableLiveData<MemoryScreenState> = MutableLiveData(MemoryScreenState.Blank)
    private val memoryInfoRequestLiveData by this::_memoryInfoRequestLiveData
    private val bluetoothRequestResult: LiveData<BluetoothRequestResultStatus> = bluetoothPacketManager.bluetoothRequestResult.asLiveData()
    private val memoryInfoResultLiveData = bluetoothRequestResult.map { status: BluetoothRequestResultStatus ->
        getBluetoothReceivedDataRequestStatus(status)
    }

    init {
        memoryInfoLiveData.addSource(memoryInfoRequestLiveData) { value ->
            memoryInfoLiveData.value = value
        }

        memoryInfoLiveData.addSource(memoryInfoResultLiveData) { value ->
            memoryInfoLiveData.value = value
        }
    }

    private fun getBluetoothReceivedDataRequestStatus(bluetoothRequestResultStatus: BluetoothRequestResultStatus): MemoryScreenState {
        return when(bluetoothRequestResultStatus) {
            is BluetoothRequestResultStatus.MemoryServiceDataReceived -> renderMemoryServiceData(
                memoryServiceDataModel = bluetoothRequestResultStatus.memoryServiceDataModel)
            is BluetoothRequestResultStatus.MemoryDataReceived -> TODO()
            is BluetoothRequestResultStatus.MemoryClearResult -> renderMemoryClearData(
                isCleared = bluetoothRequestResultStatus.isCleared)
            else -> MemoryScreenState.Error("Неверные данные")
        }
    }

    private fun renderMemoryServiceData(memoryServiceDataModel: MemoryServiceDataModel): MemoryScreenState {
        with(memoryServiceDataModel) {
            currentMemoryAddress = currentAddress
            sensorsNum = sensorsNumber
            localIds = localIdList
            sensorsLetterCodes = sensorsLetterCodeList
            sensorIds = sensorIdsList
        }
        memoryAddressCounter = 0
        sensorHistoryDataModelList.clear()
        return MemoryScreenState.ServiceDataAnswer("Загрузка данных: датчиков - ${memoryServiceDataModel.sensorsNumber}, " +
                "объём принимаемых данных - ${memoryServiceDataModel.currentAddress}...")
    }

    private fun renderMemoryData(memoryServiceDataModel: MemoryServiceDataModel): MemoryScreenState {
        TODO()
    }

    private fun renderMemoryClearData(isCleared: Boolean): MemoryScreenState {
        timeIntervalJob.cancel()
        return if (isCleared) MemoryScreenState.MemoryClearSuccess
        else MemoryScreenState.Error("Ошибка! Не удалось очистить память.")
    }
    
    fun clearMemory(coroutineScope: CoroutineScope) {
        Log.d(TAG, "clearMemory: ")
        timeIntervalJob.start()
        lifecycleScope = coroutineScope
        lifecycleScope.launch {
            sendClearMemoryRequest()
        }
    }

    fun getMemoryInfo(coroutineScope: CoroutineScope) {
        lifecycleScope = coroutineScope
        timeIntervalJob.start()
        lifecycleScope.launch {
            sendMemoryInfoRequest()
        }
    }

    private suspend fun sendClearMemoryRequest() {
        if (bluetoothRepository.isDeviceConnected) {
            _memoryInfoRequestLiveData.value = MemoryScreenState.MemoryClearExecution
            val writeSuccess = bluetoothRepository.writeByteArray(clearMemoryRequestPacket)
            if (!writeSuccess) _memoryInfoRequestLiveData.value = MemoryScreenState.Error("Не удалось отправить команду на стирание памяти термометра")
        } else _memoryInfoRequestLiveData.value = MemoryScreenState.Error("Нет связи с термометром")
    }

    private suspend fun sendMemoryInfoRequest() {
        if (bluetoothRepository.isDeviceConnected) {
            _memoryInfoRequestLiveData.value = MemoryScreenState.MemoryInfoLoad
            val writeSuccess = bluetoothRepository.writeByteArray(currentMemoryAddressRequestPacket)
            if (!writeSuccess) _memoryInfoRequestLiveData.value = MemoryScreenState.Error("Не удалось отправить команду на получение информации о памяти термометра")
        } else _memoryInfoRequestLiveData.value = MemoryScreenState.Error("Нет связи с термометром")
    }

}
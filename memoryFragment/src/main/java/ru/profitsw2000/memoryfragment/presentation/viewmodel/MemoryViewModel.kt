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
import ru.profitsw2000.data.model.MemoryInfoModel
import ru.profitsw2000.data.model.MemoryServiceDataModel
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.model.state.MemoryScreenState
import ru.profitsw2000.data.model.state.SensorInfoState
import ru.profitsw2000.data.model.state.memoryscreen.MemoryClearState
import ru.profitsw2000.data.model.state.memoryscreen.MemoryDataLoadState
import ru.profitsw2000.data.model.state.memoryscreen.MemoryInfoState
import ru.profitsw2000.data.model.status.BluetoothRequestResultStatus
import kotlin.getValue

class MemoryViewModel(
    private val bluetoothRepository: BluetoothRepository,
    private val bluetoothPacketManager: BluetoothPacketManager
) : ViewModel() {
    //coroutine
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var lifecycleScope: CoroutineScope
    private val memoryInfoRequestTimeIntervalJob = coroutineScope.launch(start = CoroutineStart.LAZY) {
        delay(MEMORY_DATA_PACKET_TIMEOUT_INTERVAL)
        _memoryInfoRequestLiveData.value = MemoryInfoState.MemoryInfoTimeoutError
    }

    //memory parameters
    private var currentMemoryAddress: Int = 0
    private var sensorsNum: Int = 0
    private var localIds: List<Int> = arrayListOf()
    private var sensorsLetterCodes: List<Int> = arrayListOf()
    private var sensorIds: List<ULong> = arrayListOf()
    private var sensorHistoryDataModelList: MutableList<SensorHistoryDataModel> = mutableListOf()
    private var memoryAddressCounter = 0

    private val bluetoothRequestResult: LiveData<BluetoothRequestResultStatus> = bluetoothPacketManager.bluetoothRequestResult.asLiveData()
    //информация о памяти термометра
    var memoryInfoLiveData = MediatorLiveData<MemoryInfoState>()
    private var _memoryInfoRequestLiveData: MutableLiveData<MemoryInfoState> = MutableLiveData(
        MemoryInfoState.MemoryInfoInitialState)
    private val memoryInfoRequestLiveData by this::_memoryInfoRequestLiveData
    private val memoryInfoResultLiveData = bluetoothRequestResult.map { status: BluetoothRequestResultStatus ->
        getMemoryInfoReceivedByBluetooth(status)
    }
    //очистка памяти термометра
    var memoryClearLiveData = MediatorLiveData<MemoryClearState>()
    private var _memoryClearRequestLiveData: MutableLiveData<MemoryClearState> = MutableLiveData(
        MemoryClearState.MemoryClearInitialState)
    private val memoryClearRequestLiveData by this::_memoryClearRequestLiveData
    private val memoryClearResultLiveData = bluetoothRequestResult.map { status: BluetoothRequestResultStatus ->
        getMemoryClearResultReceivedByBluetooth(status)
    }
    //загрузка данных из памяти термометра
    var memoryLoadLiveData = MediatorLiveData<MemoryDataLoadState>()
    private var _memoryLoadRequestLiveData: MutableLiveData<MemoryDataLoadState> = MutableLiveData(
        MemoryDataLoadState.MemoryDataLoadInitialState)
    private val memoryLoadRequestLiveData by this::_memoryLoadRequestLiveData
    private val memoryLoadResultLiveData = bluetoothRequestResult.map { status: BluetoothRequestResultStatus ->
        getMemoryLoadDataReceivedByBluetooth(status)
    }


    init {
        memoryInfoLiveData.addSource(memoryInfoRequestLiveData) { value ->
            memoryInfoLiveData.value = value
        }
        memoryInfoLiveData.addSource(memoryInfoResultLiveData) { value ->
            memoryInfoLiveData.value = value
        }

        memoryClearLiveData.addSource(memoryClearRequestLiveData) { value ->
            memoryClearLiveData.value = value
        }
        memoryClearLiveData.addSource(memoryClearResultLiveData) { value ->
            memoryClearLiveData.value = value
        }

        memoryLoadLiveData.addSource(memoryLoadRequestLiveData) { value ->
            memoryLoadLiveData.value = value
        }
        memoryLoadLiveData.addSource(memoryLoadResultLiveData) { value ->
            memoryLoadLiveData.value = value
        }
    }

    private fun getMemoryInfoReceivedByBluetooth(
        bluetoothRequestResultStatus: BluetoothRequestResultStatus
    ): MemoryInfoState {
        return when(bluetoothRequestResultStatus) {
            is BluetoothRequestResultStatus.CurrentMemorySpace -> renderMemorySpaceInfo(
                memoryInfoModel = bluetoothRequestResultStatus.memoryInfoModel)
            else -> memoryInfoLiveData.value!!
        }
    }

    private fun getMemoryClearResultReceivedByBluetooth(
        bluetoothRequestResultStatus: BluetoothRequestResultStatus
    ): MemoryClearState {
        return when(bluetoothRequestResultStatus) {
            is BluetoothRequestResultStatus.MemoryClearResult -> renderMemoryClearData(
                isCleared = bluetoothRequestResultStatus.isCleared
            )
            else -> memoryClearLiveData.value!!
        }
    }

    private fun getMemoryLoadDataReceivedByBluetooth(
        bluetoothRequestResultStatus: BluetoothRequestResultStatus
    ): MemoryDataLoadState {
        return when(bluetoothRequestResultStatus) {
            is BluetoothRequestResultStatus.MemoryDataReceived -> TODO()
            is BluetoothRequestResultStatus.MemoryServiceDataReceived -> TODO()
            else -> memoryLoadLiveData.value!!
        }
    }

    private fun renderMemorySpaceInfo(memoryInfoModel: MemoryInfoModel): MemoryInfoState {
        memoryInfoRequestTimeIntervalJob.cancel()
        return MemoryInfoState.MemoryInfoSuccess(memoryInfoModel)
    }

    private fun renderMemoryClearData(isCleared: Boolean): MemoryClearState {
        memoryInfoRequestTimeIntervalJob.cancel()
        return if (isCleared) MemoryClearState.MemoryClearSuccess
        else MemoryClearState.MemoryClearError
    }

    private fun renderMemoryServiceData(memoryServiceDataModel: MemoryServiceDataModel): MemoryDataLoadState {
        with(memoryServiceDataModel) {
            currentMemoryAddress = currentAddress
            sensorsNum = sensorsNumber
            localIds = localIdList
            sensorsLetterCodes = sensorsLetterCodeList
            sensorIds = sensorIdsList
        }
        memoryAddressCounter = 0
        sensorHistoryDataModelList.clear()
        return MemoryDataLoadState.ServiceDataReceived(memoryServiceDataModel.sensorsNumber, memoryServiceDataModel.currentAddress)
    }

    private fun renderMemoryData(memoryServiceDataModel: MemoryServiceDataModel): MemoryDataLoadState {
        TODO()
    }

    fun getMemoryInfo(coroutineScope: CoroutineScope) {
        lifecycleScope = coroutineScope
        memoryInfoRequestTimeIntervalJob.start()
        lifecycleScope.launch {
            sendMemoryInfoRequest()
        }
    }

    private suspend fun sendMemoryInfoRequest() {
        if (bluetoothRepository.isDeviceConnected) {
            _memoryInfoRequestLiveData.value = MemoryInfoState.MemoryInfoLoad
            val writeSuccess = bluetoothRepository.writeByteArray(currentMemoryAddressRequestPacket)
            if (!writeSuccess) _memoryInfoRequestLiveData.value = MemoryInfoState.MemoryInfoSendRequestError
        } else _memoryInfoRequestLiveData.value = MemoryInfoState.MemoryInfoDeviceConnectionError
    }

    fun clearMemory(coroutineScope: CoroutineScope) {
        Log.d(TAG, "clearMemory: ")
        memoryInfoRequestTimeIntervalJob.start()
        lifecycleScope = coroutineScope
        lifecycleScope.launch {
            sendClearMemoryRequest()
        }
    }

    private suspend fun sendClearMemoryRequest() {
        if (bluetoothRepository.isDeviceConnected) {
            _memoryClearRequestLiveData.value = MemoryClearState.MemoryClearExecution
            val writeSuccess = bluetoothRepository.writeByteArray(clearMemoryRequestPacket)
            if (!writeSuccess) _memoryClearRequestLiveData.value = MemoryClearState.MemoryClearSendRequestError
        } else _memoryClearRequestLiveData.value = MemoryClearState.MemoryClearDeviceConnectionError
    }

    fun setMemoryInfoToInitialState() {
        memoryInfoRequestLiveData.value = MemoryInfoState.MemoryInfoInitialState
    }

}
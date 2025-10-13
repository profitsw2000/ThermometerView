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
import kotlinx.coroutines.launch
import ru.profitsw2000.core.utils.constants.MEMORY_DATA_PACKET_TIMEOUT_INTERVAL
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.core.utils.constants.clearMemoryRequestPacket
import ru.profitsw2000.core.utils.constants.currentMemoryAddressRequestPacket
import ru.profitsw2000.core.utils.constants.memoryLoadDataPacket
import ru.profitsw2000.core.utils.constants.memoryLoadFirstDataPacket
import ru.profitsw2000.core.utils.constants.memoryLoadServicePacket
import ru.profitsw2000.core.utils.constants.memoryLoadStopDataTransferPacket
import ru.profitsw2000.data.domain.BluetoothPacketManager
import ru.profitsw2000.data.domain.BluetoothRepository
import ru.profitsw2000.data.model.MemoryDataModel
import ru.profitsw2000.data.model.MemoryInfoModel
import ru.profitsw2000.data.model.MemoryServiceDataModel
import ru.profitsw2000.data.model.SensorHistoryDataModel
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
    private val memoryClearRequestTimeIntervalJob = coroutineScope.launch(start = CoroutineStart.LAZY) {
        delay(MEMORY_DATA_PACKET_TIMEOUT_INTERVAL)
        _memoryClearRequestLiveData.value = MemoryClearState.MemoryClearTimeoutError
    }
    private val memoryDataLoadRequestTimeIntervalJob = coroutineScope.launch(start = CoroutineStart.LAZY) {
        delay(MEMORY_DATA_PACKET_TIMEOUT_INTERVAL)
        _memoryLoadRequestLiveData.value = MemoryDataLoadState.MemoryDataLoadTimeoutError(memoryLoadLiveData.value!!)
    }

    //memory parameters
    private var currentMemoryAddress: Int = 0
    private var sensorsNum: Int = 0
    private var localIds: List<Int> = arrayListOf()
    private var sensorsLetterCodes: List<Int> = arrayListOf()
    private var sensorIds: List<ULong> = arrayListOf()
    private var sensorHistoryDataModelList: MutableList<SensorHistoryDataModel> = mutableListOf()
    private var memoryAddressCounter = 0
    private var needToClearMemory = false

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
            is BluetoothRequestResultStatus.MemoryDataReceived -> renderMemoryData(
                bluetoothRequestResultStatus.memoryDataModel
            )
            is BluetoothRequestResultStatus.MemoryServiceDataReceived -> renderMemoryServiceData(
                bluetoothRequestResultStatus.memoryServiceDataModel
            )
            is BluetoothRequestResultStatus.MemoryStopDataTransfer -> getFinalState()
            else -> memoryLoadLiveData.value!!
        }
    }

    private fun renderMemorySpaceInfo(memoryInfoModel: MemoryInfoModel): MemoryInfoState {
        memoryInfoRequestTimeIntervalJob.cancel()
        return MemoryInfoState.MemoryInfoSuccess(memoryInfoModel)
    }

    private fun renderMemoryClearData(isCleared: Boolean): MemoryClearState {
        memoryClearRequestTimeIntervalJob.cancel()
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
        return if (localIds.size == sensorsLetterCodes.size
            && sensorsLetterCodes.size == sensorIds.size){
            MemoryDataLoadState.ServiceDataReceived(memoryServiceDataModel.sensorsNumber, memoryServiceDataModel.currentAddress)
        } else MemoryDataLoadState.InvalidMemoryData(memoryLoadLiveData.value!!)
    }

    private fun renderMemoryData(memoryDataModel: MemoryDataModel): MemoryDataLoadState {
        val localId = memoryDataModel.localId
        return if (localIds.contains(localId)) {
            val sensorId = sensorIds[localId - 1]
            val letterCode = sensorsLetterCodes[localId - 1]
            val dateTime = memoryDataModel.dateTime
            val temperature = memoryDataModel.temperature

            sensorHistoryDataModelList.add(
                SensorHistoryDataModel(
                    localId = localId,
                    sensorId = sensorId,
                    letterCode = letterCode,
                    date = dateTime,
                    temperature = temperature
                )
            )
            memoryAddressCounter += 8
            MemoryDataLoadState.MemoryDataReceived(
                percentProgress = memoryAddressCounter.toFloat()/currentMemoryAddress.toFloat()
            )
        } else MemoryDataLoadState.InvalidMemoryData(memoryLoadLiveData.value!!)
    }

    private fun getFinalState(): MemoryDataLoadState {
        return if (memoryLoadLiveData.value == MemoryDataLoadState.MemoryDataLoadStopRequest) {
            MemoryDataLoadState.MemoryDataLoadInterrupted
        } else {
            MemoryDataLoadState.MemoryDataLoadCompleted
        }
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
        memoryClearRequestTimeIntervalJob.start()
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

    fun startMemoryDataLoad(
        coroutineScope: CoroutineScope,
        clearMemory: Boolean
    ) {
        needToClearMemory = clearMemory
        loadMemoryServiceDataPacket(coroutineScope)
    }

    fun continueMemoryDataLoad(
        coroutineScope: CoroutineScope,
        memoryDataLoadState: MemoryDataLoadState
    ) {
        when(memoryDataLoadState) {
            is MemoryDataLoadState.InvalidMemoryData -> selectMemoryDataPacket(memoryDataLoadState)
            MemoryDataLoadState.MemoryDataLoadCompleted -> TODO()
            MemoryDataLoadState.MemoryDataLoadDeviceConnectionError -> TODO()
            MemoryDataLoadState.MemoryDataLoadInitialState -> TODO()
            MemoryDataLoadState.MemoryDataLoadInterrupted -> TODO()
            is MemoryDataLoadState.MemoryDataLoadRequestError -> TODO()
            MemoryDataLoadState.MemoryDataLoadStopRequest -> TODO()
            is MemoryDataLoadState.MemoryDataLoadTimeoutError -> TODO()
            is MemoryDataLoadState.MemoryDataReceived -> TODO()
            is MemoryDataLoadState.MemoryDataRequest -> TODO()
            is MemoryDataLoadState.ServiceDataReceived -> TODO()
            MemoryDataLoadState.ServiceDataRequest -> TODO()
            null -> TODO()
        }
    }

    fun loadMemoryServiceDataPacket(coroutineScope: CoroutineScope) {
        if (memoryLoadLiveData.value == MemoryDataLoadState.MemoryDataLoadInitialState) {
            memoryDataLoadRequestTimeIntervalJob.start()
            lifecycleScope = coroutineScope
            lifecycleScope.launch {
                sendLoadMemoryDataRequest(memoryLoadServicePacket, MemoryDataLoadState.ServiceDataRequest)
            }
        }
    }

    fun loadFirstMemoryDataPacket(coroutineScope: CoroutineScope) {
        val loadPercentage = if (currentMemoryAddress != 0) memoryAddressCounter.toFloat()/currentMemoryAddress.toFloat()
        else 0f

        memoryDataLoadRequestTimeIntervalJob.start()
        lifecycleScope = coroutineScope
        lifecycleScope.launch {
            sendLoadMemoryDataRequest(
                memoryLoadFirstDataPacket,
                MemoryDataLoadState.MemoryDataRequest(loadPercentage)
            )
        }
    }

    fun loadNextMemoryDataPacket(coroutineScope: CoroutineScope) {
        val loadPercentage = if (currentMemoryAddress != 0) memoryAddressCounter.toFloat()/currentMemoryAddress.toFloat()
        else 0f

        memoryDataLoadRequestTimeIntervalJob.start()
        lifecycleScope = coroutineScope
        lifecycleScope.launch {
            sendLoadMemoryDataRequest(
                memoryLoadDataPacket,
                MemoryDataLoadState.MemoryDataRequest(loadPercentage)
            )
        }
    }

    fun stopMemoryLoadPacket(coroutineScope: CoroutineScope) {
        memoryDataLoadRequestTimeIntervalJob.start()
        lifecycleScope = coroutineScope
        lifecycleScope.launch {
            sendLoadMemoryDataRequest(memoryLoadStopDataTransferPacket, MemoryDataLoadState.MemoryDataLoadStopRequest)
        }
    }

    private suspend fun sendLoadMemoryDataRequest(
        byteArray: ByteArray,
        memoryDataLoadState: MemoryDataLoadState
    ) {
        if (bluetoothRepository.isDeviceConnected) {
            _memoryLoadRequestLiveData.value = memoryDataLoadState
            val writeSuccess = bluetoothRepository.writeByteArray(byteArray)
            if (!writeSuccess) _memoryLoadRequestLiveData.value = MemoryDataLoadState.MemoryDataLoadRequestError(memoryDataLoadState)
        } else _memoryLoadRequestLiveData.value = MemoryDataLoadState.MemoryDataLoadDeviceConnectionError
    }

    fun setMemoryInfoToInitialState() {
        memoryInfoRequestLiveData.value = MemoryInfoState.MemoryInfoInitialState
    }

    fun setMemoryClearToInitialState() {
        memoryClearLiveData.value = MemoryClearState.MemoryClearInitialState
    }

    fun setMemoryDataLoadToInitialState() {
        memoryLoadLiveData.value = MemoryDataLoadState.MemoryDataLoadInitialState
    }

}
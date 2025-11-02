package ru.profitsw2000.memoryfragment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
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
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.data.model.MemoryDataModel
import ru.profitsw2000.data.model.MemoryInfoModel
import ru.profitsw2000.data.model.MemoryServiceDataModel
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.model.state.memoryscreen.MemoryClearState
import ru.profitsw2000.data.model.state.memoryscreen.MemoryDataLoadState
import ru.profitsw2000.data.model.state.memoryscreen.MemoryInfoState
import ru.profitsw2000.data.model.status.BluetoothRequestResultStatus
import kotlin.getValue

const val ATTEMPTS_NUMBER = 10

class MemoryViewModel(
    private val bluetoothRepository: BluetoothRepository,
    private val bluetoothPacketManager: BluetoothPacketManager,
    private val sensorHistoryInteractor: SensorHistoryInteractor,
    private val sensorHistoryMapper: SensorHistoryMapper
) : ViewModel() {
    //coroutine
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var lifecycleScope: CoroutineScope
    private var requestTimeIntervalJob: Job? = null
    private var sequentialTimeoutErrors = 0

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
    private val memoryInfoRequestLiveData: LiveData<MemoryInfoState> by this::_memoryInfoRequestLiveData
    private val memoryInfoResultLiveData = bluetoothRequestResult.map { status: BluetoothRequestResultStatus ->
        getMemoryInfoReceivedByBluetooth(status)
    }
    //очистка памяти термометра
    var memoryClearLiveData = MediatorLiveData<MemoryClearState>()
    private var _memoryClearRequestLiveData: MutableLiveData<MemoryClearState> = MutableLiveData(
        MemoryClearState.MemoryClearInitialState)
    private val memoryClearRequestLiveData: LiveData<MemoryClearState> by this::_memoryClearRequestLiveData
    private val memoryClearResultLiveData = bluetoothRequestResult.map { status: BluetoothRequestResultStatus ->
        getMemoryClearResultReceivedByBluetooth(status)
    }
    //загрузка данных из памяти термометра
    var memoryLoadLiveData = MediatorLiveData<MemoryDataLoadState>()
    private var _memoryLoadRequestLiveData: MutableLiveData<MemoryDataLoadState> = MutableLiveData(
        MemoryDataLoadState.MemoryDataLoadInitialState)
    private val memoryLoadRequestLiveData: LiveData<MemoryDataLoadState> by this::_memoryLoadRequestLiveData
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
            else -> MemoryInfoState.MemoryInfoInitialState
        }
    }

    private fun getMemoryClearResultReceivedByBluetooth(
        bluetoothRequestResultStatus: BluetoothRequestResultStatus
    ): MemoryClearState {
        return when(bluetoothRequestResultStatus) {
            is BluetoothRequestResultStatus.MemoryClearResult -> if (memoryClearLiveData.value == MemoryClearState.MemoryClearExecution)
                renderMemoryClearData(
                    isCleared = bluetoothRequestResultStatus.isCleared
                ) else MemoryClearState.MemoryClearInitialState
            else -> MemoryClearState.MemoryClearInitialState
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
            is BluetoothRequestResultStatus.MemoryClearResult -> if (memoryLoadLiveData.value == MemoryDataLoadState.MemoryDataLoadClearRequest)
                renderMemoryDataLoadClear(
                    bluetoothRequestResultStatus.isCleared
                ) else MemoryDataLoadState.MemoryDataLoadInitialState
            else -> MemoryDataLoadState.MemoryDataLoadInitialState
        }
    }

    private fun renderMemorySpaceInfo(memoryInfoModel: MemoryInfoModel): MemoryInfoState {
        requestTimeIntervalJob?.cancel()
        return MemoryInfoState.MemoryInfoSuccess(memoryInfoModel)
    }

    private fun renderMemoryClearData(isCleared: Boolean): MemoryClearState {
        requestTimeIntervalJob?.cancel()
        return if (isCleared) MemoryClearState.MemoryClearSuccess
        else MemoryClearState.MemoryClearError
    }

    private fun renderMemoryServiceData(memoryServiceDataModel: MemoryServiceDataModel): MemoryDataLoadState {
        requestTimeIntervalJob?.cancel()
        sequentialTimeoutErrors = 0
        with(memoryServiceDataModel) {
            currentMemoryAddress = currentAddress
            sensorsNum = sensorsNumber
            localIds = localIdList
            sensorsLetterCodes = sensorsLetterCodeList
            sensorIds = sensorIdsList
        }
        memoryAddressCounter = 0
        sensorHistoryDataModelList.clear()
        loadFirstMemoryDataPacket()
        return if (localIds.size == sensorsLetterCodes.size
            && sensorsLetterCodes.size == sensorIds.size){
            MemoryDataLoadState.MemoryHistoryDataLoading(
                percentProgress = (memoryAddressCounter.toFloat()/currentMemoryAddress.toFloat())*100f,
                memoryAddressCounter,
                currentMemoryAddress
            )
        } else MemoryDataLoadState.InvalidMemoryData(MemoryDataLoadState.MemoryServiceDataLoading)
    }

    private fun renderMemoryData(memoryDataModel: MemoryDataModel): MemoryDataLoadState {
        val localId = memoryDataModel.localId
        requestTimeIntervalJob?.cancel()
        sequentialTimeoutErrors = 0
        return if (localIds.contains(localId)) {
            val sensorId = sensorIds[localId - 1].toLong()
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
            loadNextMemoryDataPacket()
            MemoryDataLoadState.MemoryHistoryDataLoading(
                percentProgress = (memoryAddressCounter.toFloat()/currentMemoryAddress.toFloat())*100f,
                memoryAddressCounter,
                currentMemoryAddress
            )
        } else MemoryDataLoadState.InvalidMemoryData(memoryLoadLiveData.value!!)
    }

    private fun getFinalState(): MemoryDataLoadState {
        requestTimeIntervalJob?.cancel()
        return if (memoryLoadLiveData.value == MemoryDataLoadState.MemoryDataLoadStopRequest) {
            MemoryDataLoadState.MemoryDataLoadInterrupted
        } else {
            MemoryDataLoadState.MemoryDataLoadCompleted
        }
    }

    private fun renderMemoryDataLoadClear(isCleared: Boolean): MemoryDataLoadState {
        requestTimeIntervalJob?.cancel()
        return if(isCleared) MemoryDataLoadState.MemoryDataLoadClearSuccess
        else MemoryDataLoadState.InvalidMemoryData(MemoryDataLoadState.MemoryDataLoadClearRequest)
    }

    private fun memoryInfoTimeoutIntervalJob() {
        requestTimeIntervalJob = ioCoroutineScope.launch {
            delay(MEMORY_DATA_PACKET_TIMEOUT_INTERVAL)
            lifecycleScope.launch {
                _memoryInfoRequestLiveData.value = MemoryInfoState.MemoryInfoTimeoutError
            }
        }
    }

    private fun memoryClearTimeoutIntervalJob() {
        requestTimeIntervalJob = ioCoroutineScope.launch {
            delay(MEMORY_DATA_PACKET_TIMEOUT_INTERVAL)
            lifecycleScope.launch {
                _memoryClearRequestLiveData.value = MemoryClearState.MemoryClearTimeoutError
            }
        }
    }

    private fun memoryDataLoadTimeoutIntervalJob() {
        requestTimeIntervalJob = ioCoroutineScope.launch {
            delay(MEMORY_DATA_PACKET_TIMEOUT_INTERVAL)
            sequentialTimeoutErrors++
            if (sequentialTimeoutErrors < ATTEMPTS_NUMBER) {
                continueMemoryDataLoad(memoryLoadLiveData.value!!)
            } else {
                sequentialTimeoutErrors = 0
                lifecycleScope.launch {
                    _memoryLoadRequestLiveData.value = MemoryDataLoadState.MemoryDataLoadTimeoutError(memoryLoadLiveData.value!!)
                }
            }
        }
    }

    fun getMemoryInfo() {
        memoryInfoTimeoutIntervalJob()
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

    fun clearMemory() {
        memoryClearTimeoutIntervalJob()
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

    fun startMemoryDataLoad(clearMemory: Boolean) {
        needToClearMemory = clearMemory
        loadMemoryServiceDataPacket()
    }

    fun continueMemoryDataLoad(memoryDataLoadState: MemoryDataLoadState) {
        Log.d(TAG, "continueMemoryDataLoad: /////////////////////////////////////////////////////////////////////////////////////")
        when(memoryDataLoadState) {
            is MemoryDataLoadState.MemoryHistoryDataLoading -> loadFirstMemoryDataPacket()
            MemoryDataLoadState.MemoryDataLoadClearRequest -> memoryDataLoadClear()
            MemoryDataLoadState.MemoryDataLoadDatabaseWriteExecution -> writeLoadedMemoryToDatabase()
            MemoryDataLoadState.MemoryServiceDataLoading -> loadMemoryServiceDataPacket()
            else -> loadMemoryServiceDataPacket()
        }
    }

    fun memoryDataLoadClear() {
        if (needToClearMemory) {
            memoryDataLoadTimeoutIntervalJob()
            lifecycleScope.launch {
                sendLoadMemoryDataRequest(clearMemoryRequestPacket, MemoryDataLoadState.MemoryDataLoadClearRequest)
            }
        } else _memoryLoadRequestLiveData.value = MemoryDataLoadState.MemoryDataLoadSuccess
    }

    fun loadMemoryServiceDataPacket() {
        memoryDataLoadTimeoutIntervalJob()
        lifecycleScope.launch {
            sendLoadMemoryDataRequest(memoryLoadServicePacket, MemoryDataLoadState.MemoryServiceDataLoading)
        }
    }

    fun loadFirstMemoryDataPacket() {
        val loadPercentage = if (currentMemoryAddress != 0) (memoryAddressCounter.toFloat()/currentMemoryAddress.toFloat())*100f
        else 0f

        memoryDataLoadTimeoutIntervalJob()
        lifecycleScope.launch {
            sendLoadMemoryDataRequest(
                memoryLoadFirstDataPacket,
                MemoryDataLoadState.MemoryHistoryDataLoading(loadPercentage, memoryAddressCounter, currentMemoryAddress)
            )
        }
    }

    fun loadNextMemoryDataPacket() {
        val loadPercentage = if (currentMemoryAddress != 0) (memoryAddressCounter.toFloat()/currentMemoryAddress.toFloat())*100f
        else 0f

        memoryDataLoadTimeoutIntervalJob()
        lifecycleScope.launch {
            sendLoadMemoryDataRequest(
                memoryLoadDataPacket,
                MemoryDataLoadState.MemoryHistoryDataLoading(loadPercentage, memoryAddressCounter, currentMemoryAddress)
            )
        }
    }

    fun checkMemoryLoadAndStop() {
        if (memoryLoadLiveData.value != MemoryDataLoadState.MemoryDataLoadInitialState) {
            lifecycleScope.launch {
                val writeSuccess = bluetoothRepository.writeByteArray(memoryLoadStopDataTransferPacket)
            }
        }
    }

    fun writeLoadedMemoryToDatabase() {
        if (sensorHistoryDataModelList.isNotEmpty()) {
            _memoryLoadRequestLiveData.value = MemoryDataLoadState.MemoryDataLoadSuccess
        } else _memoryLoadRequestLiveData.value = MemoryDataLoadState.MemoryDataLoadSuccess
    }

    private suspend fun insertHistoryDataList(): MemoryDataLoadState {
        val deferred: Deferred<MemoryDataLoadState> = ioCoroutineScope.async {
            try {
                sensorHistoryInteractor.writeHistoryItemList(sensorHistoryMapper.map(sensorHistoryDataModelList) , false)
                MemoryDataLoadState.MemoryDataLoadDatabaseWriteSuccess
            } catch (exception: Exception) {
                Log.d(TAG, "insertHistoryDataList: ${exception.message}")
                MemoryDataLoadState.MemoryDataLoadDatabaseWriteError(MemoryDataLoadState.MemoryDataLoadDatabaseWriteExecution)
            }
        }
        return deferred.await()
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

    fun isDataExchange(): Boolean {
        return !(memoryInfoRequestLiveData.value == MemoryInfoState.MemoryInfoInitialState &&
                memoryClearLiveData.value == MemoryClearState.MemoryClearInitialState &&
                memoryLoadLiveData.value == MemoryDataLoadState.MemoryDataLoadInitialState)
    }

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.lifecycleScope = coroutineScope
    }

    fun setMemoryInfoToInitialState() {
        _memoryInfoRequestLiveData.value = MemoryInfoState.MemoryInfoInitialState
    }

    fun setMemoryClearToInitialState() {
        _memoryClearRequestLiveData.value = MemoryClearState.MemoryClearInitialState
    }

    fun setMemoryDataLoadToInitialState() {
        _memoryLoadRequestLiveData.value = MemoryDataLoadState.MemoryDataLoadInitialState
    }

    fun setInitialState() {
        lifecycleScope.cancel()
        setMemoryInfoToInitialState()
        setMemoryClearToInitialState()
        setMemoryDataLoadToInitialState()
    }

}
package ru.profitsw2000.memoryfragment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import ru.profitsw2000.core.utils.constants.TAG
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
        Log.d(TAG, "MemoryViewModel init block!!!")
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
        return if (isCleared) MemoryScreenState.MemoryClearSuccess
        else MemoryScreenState.Error("Ошибка! Не удалось очистить память.")
    }
    
    fun clearMemory() {
        Log.d(TAG, "clearMemory: ")
    }

}
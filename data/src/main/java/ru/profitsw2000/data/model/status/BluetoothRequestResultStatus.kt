package ru.profitsw2000.data.model.status

import ru.profitsw2000.data.model.MemoryDataModel
import ru.profitsw2000.data.model.MemoryInfoModel
import ru.profitsw2000.data.model.MemoryServiceDataModel
import ru.profitsw2000.data.model.SensorModel

sealed class BluetoothRequestResultStatus {
    data class DateTimeInfo(val dateTimeString: String) : BluetoothRequestResultStatus()
    data class CurrentMemorySpace(val memoryInfoModel: MemoryInfoModel) : BluetoothRequestResultStatus()
    data class SensorsCurrentInfo(val sensorModelList: List<SensorModel>) : BluetoothRequestResultStatus()
    data class SensorInfo(val sensorModel: SensorModel) : BluetoothRequestResultStatus()
    data class MemoryServiceDataReceived(val memoryServiceDataModel: MemoryServiceDataModel) : BluetoothRequestResultStatus()
    data class MemoryDataReceived(val memoryDataModel: MemoryDataModel): BluetoothRequestResultStatus()
    data object Error : BluetoothRequestResultStatus()
}
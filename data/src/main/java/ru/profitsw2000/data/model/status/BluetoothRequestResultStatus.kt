package ru.profitsw2000.data.model.status

import ru.profitsw2000.data.model.MemoryInfoModel
import ru.profitsw2000.data.model.SensorModel

sealed class BluetoothRequestResultStatus {
    data class DateTimeInfo(val dateTimeString: String) : BluetoothRequestResultStatus()
    data class CurrentMemorySpace(val memoryInfoModel: MemoryInfoModel) : BluetoothRequestResultStatus()
    data class SensorsCurrentInfo(val sensorModelList: List<SensorModel>) : BluetoothRequestResultStatus()
    data object Error : BluetoothRequestResultStatus()
}
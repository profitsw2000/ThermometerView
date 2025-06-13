package ru.profitsw2000.data.model

sealed class BluetoothRequestResultStatus {
    data class DateTimeInfo(val dateTimeString: String) : BluetoothRequestResultStatus()
    data class CurrentMemorySpace(val memoryInfoModel: MemoryInfoModel) : BluetoothRequestResultStatus()
    data class SensorsCurrentInfo(val sensorModelList: List<SensorModel>) : BluetoothRequestResultStatus()
    data object Error : BluetoothRequestResultStatus()
}
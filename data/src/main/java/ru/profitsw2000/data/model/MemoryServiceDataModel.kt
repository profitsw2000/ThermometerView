package ru.profitsw2000.data.model

data class MemoryServiceDataModel(
    val currentAddress: Int,
    val sensorsNumber: Int,
    val localIdList: List<Int>,
    val sensorsLetterCodeList: List<Int>,
    val sensorIdsList: List<ULong>
)

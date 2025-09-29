package ru.profitsw2000.data.model

import java.util.Date

data class SensorHistoryDataModel(
    val localId: Int,
    val sensorId: ULong,
    val letterCode: Int,
    val date: Date,
    val temperature: Double
)

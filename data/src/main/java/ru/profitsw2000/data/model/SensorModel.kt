package ru.profitsw2000.data.model

data class SensorModel(
    val sensorId: ULong,
    val sensorLocalId: Int,
    val sensorLetter: String,
    val sensorTemperature: Double,
)

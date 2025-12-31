package ru.profitsw2000.data.model

import java.util.Date

data class SensorHistoryTimeFrameDataModel(
    val sensorHistoryDataModel: SensorHistoryDataModel,
    val timeFrameStartDate: Date,
    val timeFrameEndDate: Date,
    val temperatureMutableList: MutableList<Double>
)
package ru.profitsw2000.data.model

import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import java.util.Date

data class SensorHistoryFilterModel(
    val sensorIds: List<Long> = arrayListOf(),
    val localIds: List<Int> = arrayListOf(),
    val letterCodes: List<Int> = arrayListOf(),
    val timeFrameIndex: Int = 1,
    val temperatureObtainingMethod: TimeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd,
    val isAscending: Boolean = true,
    val fromDate: Date? = null,
    val toDate: Date? = null
)

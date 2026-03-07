package ru.profitsw2000.data.domain.filter

import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod

interface SensorHistoryGraphFilterRepository : SensorHistoryFilterRepository {
    var sensorIdList: List<Long>
    var letterCodeList: List<Int>
    var timeFrameMillis: Long
    var timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod
}
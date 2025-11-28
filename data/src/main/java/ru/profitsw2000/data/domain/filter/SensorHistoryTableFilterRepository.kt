package ru.profitsw2000.data.domain.filter

import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod

interface SensorHistoryTableFilterRepository : SensorHistoryFilterRepository {
    var sensorIdList: List<Long>
    var localIdList: List<Int>
    var letterCodeList: List<String>
    var timeFrameFactor: Int
    var timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod
    var isAscendingOreder: Boolean
}
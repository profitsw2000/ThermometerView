package ru.profitsw2000.data.data.filter

import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import java.util.Date

class SensorHistoryTableFilterRepositoryImpl : SensorHistoryTableFilterRepository {
    override var sensorIdList: List<Long> = arrayListOf()
    override var localIdList: List<Int> = arrayListOf()
    override var letterCodeList: List<String> = arrayListOf()
    override var timeFrameFactor: Int = 1
    override var timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd
    override var isAscendingOreder: Boolean = true
    override var fromDate: Date? = null
    override var toDate: Date? = null
}
package ru.profitsw2000.data.data.filter

import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import java.util.Date

class SensorHistoryGraphFilterRepositoryImpl : SensorHistoryGraphFilterRepository {
    override var sensorIdList: List<Long> = arrayListOf()
    override var letterCodeList: List<Int> = arrayListOf()
    override var timeFrameMillis: Long = TEN_MINUTES_FRAME_MILLIS
    override var timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd
    override var fromDate: Date? = null
    override var toDate: Date? = null
}
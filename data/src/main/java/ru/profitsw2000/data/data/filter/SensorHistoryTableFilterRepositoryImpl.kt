package ru.profitsw2000.data.data.filter

import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import java.util.Date

class SensorHistoryTableFilterRepositoryImpl : SensorHistoryTableFilterRepository {
    override var sensorIdList: List<Long> = arrayListOf()
    override var localIdList: List<Int> = arrayListOf()
    override var letterCodeList: List<Int> = arrayListOf()
    override var timeFrameFactor: Int = 1
    override var isAscendingOreder: Boolean = true
    override var fromDate: Date? = null
    override var toDate: Date? = null
}
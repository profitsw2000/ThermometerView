package ru.profitsw2000.data.domain.filter

interface SensorHistoryTableFilterRepository : SensorHistoryFilterRepository {
    var sensorIdList: List<Long>
    var localIdList: List<Int>
    var letterCodeList: List<Int>
    var timeFrameFactor: Int
    var isAscendingOreder: Boolean
}
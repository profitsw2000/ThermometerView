package ru.profitsw2000.data.room.utils

import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod

class SensorHistoryTableQueryBuilder(
    sensorHistoryTableFilterRepository: SensorHistoryTableFilterRepository
) {

    private val dateRangePart = "WHERE (date BETWEEN ? AND ?) "
    private val fullListOrderByPart = "ORDER BY " +
            "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
            "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC "
    private val endPart = "LIMIT ? OFFSET ?"
    private val querySelectPart = getQuerySelectPart(
        sensorHistoryTableFilterRepository.timeFrameMillis,
        sensorHistoryTableFilterRepository.timeFrameDataObtainingMethod
    )
    private val dataFilterPart = "WHERE (date BETWEEN ? AND ?) ${getAdditionalConditionToDataFilterPart(
        sensorHistoryTableFilterRepository.sensorIdList,
        sensorHistoryTableFilterRepository.localIdList,
        sensorHistoryTableFilterRepository.letterCodeList
    )}"
    private val queryGroupByPart = "GROUP BY sensorId, date/? "
    private val args = mutableListOf<Any>()


    private fun getQuerySelectPart(
        timeFrameMillis: Long,
        timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod
    ): String {
        return if (timeFrameMillis == TEN_MINUTES_FRAME_MILLIS) {
            "SELECT * "
        } else {
            getTimeFrameQuerySelectPart(timeFrameDataObtainingMethod)
        }
    }

    private fun getTimeFrameQuerySelectPart(timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod): String {
        return "SELECT ${getTemperatureValueSqlStringOperator(timeFrameDataObtainingMethod)} " +
                "AS temperature, ${getDateAndIdValueSqlStringOperator(timeFrameDataObtainingMethod)} " +
                "sensorId, localId, letterCode FROM SensorHistoryDataEntity "
    }

    private fun getTemperatureValueSqlStringOperator(timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod): String {
        return when(timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> "AVG(temperature)"
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> "MAX(temperature)"
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> "MIN(temperature)"
            else -> "DISTINCT FIRST_VALUE(temperature) OVER w"
        }
    }

    private fun getDateAndIdValueSqlStringOperator(timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod): String {
        return if (timeFrameDataObtainingMethod == TimeFrameDataObtainingMethod.TimeFrameAverage ||
            timeFrameDataObtainingMethod == TimeFrameDataObtainingMethod.TimeFrameMaximum ||
            timeFrameDataObtainingMethod == TimeFrameDataObtainingMethod.TimeFrameMinimum)
            "MIN(date) AS date, MIN(id) AS id,"
        else "MIN(date) OVER w AS date, id,"
    }

    private fun getAdditionalConditionToDataFilterPart(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>
    ): String {
        return if (sensorIdList.isNotEmpty() ||
            localIdList.isNotEmpty() ||
            letterCodeList.isNotEmpty())
            "AND (sensorId IN ? OR localId IN ? OR letterCode IN ?) "
        else ""
    }
}
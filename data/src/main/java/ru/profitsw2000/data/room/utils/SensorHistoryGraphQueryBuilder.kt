package ru.profitsw2000.data.room.utils

import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import kotlin.collections.plus

private const val FIRST_QUERY = 1
private const val SECOND_QUERY = 2
private const val THIRD_QUERY = 3
private const val FOURTH_QUERY = 4
private const val FIFTH_QUERY = 5
private const val SIXTH_QUERY = 6
private const val SEVENTH_QUERY = 7
private const val EIGHTH_QUERY = 8
private const val NINTH_QUERY = 9
private const val TENTH_QUERY = 10
private const val ELEVENTH_QUERY = 11
private const val TWELVE_QUERY = 12

class SensorHistoryGraphQueryBuilder(
    val sensorHistoryGraphFilterRepository: SensorHistoryGraphFilterRepository
) {
    ///////////////////////////////////////////////////////////////////////
    //////////// QUERY NUMBER /////////////////////////////////////////////
    private fun getQueryNumber(): Int {
        return if (sensorHistoryGraphFilterRepository.sensorIdList.isNotEmpty()) {
            getQueryNumberWithSensorIdFilter()
        } else getQueryNumberWithLetterCodeFilter()
    }

    private fun getQueryNumberWithSensorIdFilter(): Int {
        return if (sensorHistoryGraphFilterRepository.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            FIRST_QUERY
        else getQueryNumberTimeFrameWithSensorIdFilter()
    }

    private fun getQueryNumberWithLetterCodeFilter(): Int {
        return if (sensorHistoryGraphFilterRepository.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            SEVENTH_QUERY
        else getQueryNumberTimeFrameWithLetterCodeFilter()
    }

    private fun getQueryNumberTimeFrameWithSensorIdFilter(): Int {
        return when(sensorHistoryGraphFilterRepository.timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> SECOND_QUERY
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> THIRD_QUERY
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> FOURTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameBegin -> FIFTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameEnd -> SIXTH_QUERY
        }
    }

    private fun getQueryNumberTimeFrameWithLetterCodeFilter(): Int {
        return when(sensorHistoryGraphFilterRepository.timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> EIGHTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> NINTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> TENTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameBegin -> ELEVENTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameEnd -> TWELVE_QUERY
        }
    }

    //QUERY PAIR
    private fun getQuerySelectPart(queryNumber: Int): Pair<String, List<Any>> {
        val pseudonymsStringQuery = "MIN(id) AS id, sensorId, localId, letterCode, MIN(date) AS date"
        val pseudonymsStringWindowQuery = "MIN(date) OVER w AS date, id, sensorId, localId, letterCode"
        val fromEntityStringQuery = "FROM SensorHistoryDataEntity"

        return when(queryNumber) {
            FIRST_QUERY, SEVENTH_QUERY ->
                Pair(
                    "SELECT * $fromEntityStringQuery ",
                    listOf()
                )
            SECOND_QUERY, EIGHTH_QUERY ->
                Pair(
                    "SELECT AVG(temperature) AS temperature, $pseudonymsStringQuery $fromEntityStringQuery ",
                    listOf()
                )
            THIRD_QUERY, NINTH_QUERY ->
                Pair(
                    "SELECT MAX(temperature) AS temperature, $pseudonymsStringQuery $fromEntityStringQuery ",
                    listOf()
                )
            FOURTH_QUERY, TENTH_QUERY ->
                Pair(
                    "SELECT MIN(temperature) AS temperature, $pseudonymsStringQuery $fromEntityStringQuery ",
                    listOf()
                )
            FIFTH_QUERY, SIXTH_QUERY, ELEVENTH_QUERY, TWELVE_QUERY ->
                Pair(
                    "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, $pseudonymsStringWindowQuery $fromEntityStringQuery ",
                    listOf()
                )
            else -> Pair("SELECT * $fromEntityStringQuery ", listOf())
        }
    }

    private fun getQueryWherePart(
        queryNumber: Int, sensorIndex: Int
    ): Pair<String, List<Any>> {

        val dateStringQuery = "AND date BETWEEN ? AND ? "
        val sensorId = if (sensorHistoryGraphFilterRepository.sensorIdList.size > sensorIndex && sensorIndex >= 0)
            sensorHistoryGraphFilterRepository.sensorIdList[sensorIndex]
        else 0L
        val letterCode = if (sensorHistoryGraphFilterRepository.letterCodeList.size > sensorIndex && sensorIndex >= 0)
            sensorHistoryGraphFilterRepository.letterCodeList[sensorIndex]
        else 0

        return when(queryNumber) {
            FIRST_QUERY, SECOND_QUERY, THIRD_QUERY, FOURTH_QUERY, FIFTH_QUERY, SIXTH_QUERY ->
                Pair(
                    first = "WHERE sensorId LIKE ? " +
                            dateStringQuery,
                    second = listOf(sensorId,
                        sensorHistoryGraphFilterRepository.fromDate?.time ?: Long.MIN_VALUE,
                        sensorHistoryGraphFilterRepository.toDate?.time ?: Long.MAX_VALUE
                    )
                )
            else -> Pair(
                first = "WHERE letterCode LIKE ? " +
                        dateStringQuery,
                second = listOf(letterCode,
                    sensorHistoryGraphFilterRepository.fromDate?.time ?: Long.MIN_VALUE,
                    sensorHistoryGraphFilterRepository.toDate?.time ?: Long.MAX_VALUE
                )
            )
        }
    }

    private fun getQueryGroupByPart(queryNumber: Int): Pair<String, List<Any>> {
        return when(queryNumber) {
            SECOND_QUERY, THIRD_QUERY, FOURTH_QUERY, FIFTH_QUERY, SIXTH_QUERY,
            EIGHTH_QUERY, NINTH_QUERY, TENTH_QUERY, ELEVENTH_QUERY, TWELVE_QUERY ->
                Pair(
                    first = "GROUP BY sensorId, date/? ",
                    second = listOf(
                        sensorHistoryGraphFilterRepository.timeFrameMillis)
                )
            else -> Pair(first = "", second = listOf())
        }
    }

    private fun getQueryWindowPart(queryNumber: Int): Pair<String, List<Any>> {
        return when(queryNumber) {
            FIFTH_QUERY, ELEVENTH_QUERY ->
                Pair(
                    first = "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date ASC) ",
                    second = listOf(sensorHistoryGraphFilterRepository.timeFrameMillis)
                )
            SIXTH_QUERY, TWELVE_QUERY ->
                Pair(
                    first = "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date DESC) ",
                    second = listOf(sensorHistoryGraphFilterRepository.timeFrameMillis)
                )
            else -> Pair(first = "", second = listOf())
        }
    }

    private fun getQueryOrderPart(): Pair<String, List<Any>> {
        return Pair(
            "ORDER BY date DESC",
            listOf()
        )
    }

    private fun getQueryLastPart(limit: Int, offset: Int): Pair<String, List<Any>> {
        return Pair(
            " LIMIT ? OFFSET ?",
            listOf(limit, offset)
        )
    }

    fun getQuery(sensorIndex: Int): Pair<String, List<Any>> {
        val queryNumber = getQueryNumber()

        val queryString = getQuerySelectPart(queryNumber).first +
                getQueryWherePart(queryNumber, sensorIndex).first +
                getQueryGroupByPart(queryNumber).first +
                getQueryWindowPart(queryNumber).first +
                getQueryOrderPart().first
        val args = getQuerySelectPart(queryNumber).second +
                getQueryWherePart(queryNumber, sensorIndex).second +
                getQueryGroupByPart(queryNumber).second +
                getQueryWindowPart(queryNumber).second +
                getQueryOrderPart().second

        return Pair(queryString, args)
    }

    fun getQuery(limit: Int, offset: Int): Pair<String, List<Any>> {
        val queryString = getQuery(0).first +
                getQueryLastPart(limit, offset).first
        val args = getQuery(0).second +
                getQueryLastPart(limit, offset).second

        return Pair(queryString, args)
    }
}
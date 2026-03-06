package ru.profitsw2000.data.room.utils

import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod

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

class SensorHistoryTableQueryBuilder(
    val sensorHistoryTableFilterRepository: SensorHistoryTableFilterRepository
) {
    ///////////////////////////////////////////////////////////////////////
    //////////// QUERY NUMBER /////////////////////////////////////////////
    private fun getQueryNumber(): Int {
        return if (sensorHistoryTableFilterRepository.sensorIdList.isEmpty()
            && sensorHistoryTableFilterRepository.localIdList.isEmpty()
            && sensorHistoryTableFilterRepository.letterCodeList.isEmpty()) {
            getQueryNumberWithoutMainFilters()
        }
        else getFilteredQueryNumber()
    }

    private fun getQueryNumberWithoutMainFilters(): Int {
        return if (sensorHistoryTableFilterRepository.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            FIRST_QUERY
        else getWithoutMainFiltersTimeFrameQueryNumber()
    }

    private fun getFilteredQueryNumber(): Int {
        return if (sensorHistoryTableFilterRepository.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            SECOND_QUERY
        else getFilteredTimeFrameQueryNumber()
    }

    private fun getWithoutMainFiltersTimeFrameQueryNumber(): Int {
        return when(sensorHistoryTableFilterRepository.timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> SEVENTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameBegin -> TENTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameEnd -> TWELVE_QUERY
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> EIGHTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> NINTH_QUERY
        }
    }

    private fun getFilteredTimeFrameQueryNumber(): Int {
        return when(sensorHistoryTableFilterRepository.timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> THIRD_QUERY
            TimeFrameDataObtainingMethod.TimeFrameBegin -> SIXTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameEnd -> ELEVENTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> FOURTH_QUERY
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> FIFTH_QUERY
        }
    }

    //QUERY PAIR
    private fun getQuerySelectPartCore(queryNumber: Int): Pair<String, List<Any>> {
        val pseudonymsStringQuery = "MIN(id) AS id, sensorId, localId, letterCode, MIN(date) AS date"
        val pseudonymsStringWindowQuery = "MIN(date) OVER w AS date, id, sensorId, localId, letterCode"
        val fromEntityStringQuery = "FROM SensorHistoryDataEntity"

        return when(queryNumber) {
            FIRST_QUERY, SECOND_QUERY ->
                Pair(
                    "SELECT * $fromEntityStringQuery ",
                    listOf()
                )
            THIRD_QUERY, SEVENTH_QUERY ->
                Pair(
                    "SELECT AVG(temperature) AS temperature, $pseudonymsStringQuery $fromEntityStringQuery ",
                    listOf()
                )
            FOURTH_QUERY, EIGHTH_QUERY ->
                Pair(
                    "SELECT MAX(temperature) AS temperature, $pseudonymsStringQuery $fromEntityStringQuery ",
                    listOf()
                )
            FIFTH_QUERY, NINTH_QUERY ->
                Pair(
                    "SELECT MIN(temperature) AS temperature, $pseudonymsStringQuery $fromEntityStringQuery ",
                    listOf()
                )
            SIXTH_QUERY, TENTH_QUERY, ELEVENTH_QUERY, TWELVE_QUERY ->
                Pair(
                    "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, $pseudonymsStringWindowQuery $fromEntityStringQuery ",
                    listOf()
                )
            else -> Pair("SELECT * $fromEntityStringQuery ", listOf())
        }
    }

    private fun getQueryWhereMainPartCore(queryNumber: Int): Pair<String, List<Any>> {

        return when(queryNumber) {
            SECOND_QUERY, THIRD_QUERY, FOURTH_QUERY, FIFTH_QUERY, SIXTH_QUERY, ELEVENTH_QUERY ->
                Pair(
                    first = "WHERE (sensorId IN (${sensorHistoryTableFilterRepository.sensorIdList.toPlaceholders()}) " +
                            "OR localId IN (${sensorHistoryTableFilterRepository.localIdList.toPlaceholders()}) " +
                            "OR letterCode IN (${sensorHistoryTableFilterRepository.letterCodeList.toPlaceholders()})) ",
                    second = sensorHistoryTableFilterRepository.sensorIdList +
                            sensorHistoryTableFilterRepository.localIdList +
                            sensorHistoryTableFilterRepository.letterCodeList
                )
            else -> Pair(
                first = "WHERE date BETWEEN ? AND ? ",
                second = listOf(
                    sensorHistoryTableFilterRepository.fromDate?.time ?: Long.MIN_VALUE,
                    sensorHistoryTableFilterRepository.toDate?.time ?: Long.MAX_VALUE)
            )
        }
    }

    private fun getQueryWhereAdditionalPartCore(queryNumber: Int): Pair<String, List<Any>> {
        return when(queryNumber) {
            SECOND_QUERY, THIRD_QUERY, FOURTH_QUERY, FIFTH_QUERY, SIXTH_QUERY, ELEVENTH_QUERY ->
                Pair(
                    first = "AND date BETWEEN ? AND ? ",
                    second = listOf(
                        sensorHistoryTableFilterRepository.fromDate?.time ?: Long.MIN_VALUE,
                        sensorHistoryTableFilterRepository.toDate?.time ?: Long.MAX_VALUE
                    )
                )
            else -> Pair(first = "", second = listOf())
        }
    }
    private fun getQueryGroupByPartCore(queryNumber: Int): Pair<String, List<Any>> {
        return when(queryNumber) {
            THIRD_QUERY, FOURTH_QUERY, FIFTH_QUERY, SIXTH_QUERY,
            SEVENTH_QUERY, EIGHTH_QUERY, NINTH_QUERY, TENTH_QUERY,
            ELEVENTH_QUERY, TWELVE_QUERY ->
                Pair(
                    first = "GROUP BY sensorId, date/? ",
                    second = listOf(
                        sensorHistoryTableFilterRepository.timeFrameMillis)
                )
            else -> Pair(first = "", second = listOf())
        }
    }

    private fun getQueryWindowPartCore(queryNumber: Int): Pair<String, List<Any>> {
        return when(queryNumber) {
            SIXTH_QUERY, TENTH_QUERY ->
                Pair(
                    first = "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY " +
                            "CASE WHEN ? THEN date END DESC, " +
                            "CASE WHEN NOT ? THEN date END ASC) ",
                    second = listOf(
                        sensorHistoryTableFilterRepository.timeFrameMillis,
                        false,
                        false
                    )
                )
            ELEVENTH_QUERY, TWELVE_QUERY ->
                Pair(
                    first = "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY " +
                            "CASE WHEN ? THEN date END DESC, " +
                            "CASE WHEN NOT ? THEN date END ASC) ",
                    second = listOf(
                        sensorHistoryTableFilterRepository.timeFrameMillis,
                        true,
                        true
                    )
                )
            else -> Pair(first = "", second = listOf())
        }
    }

    private fun getQueryLastPart(limit: Int, offset: Int): Pair<String, List<Any>> {
        return Pair(
            "ORDER BY " +
                   "CASE WHEN ? THEN date END ASC, " +
                   "CASE WHEN NOT ? THEN date END DESC " +
                   "LIMIT ? OFFSET ?",
            listOf(
                    sensorHistoryTableFilterRepository.isAscendingOrder,
                    sensorHistoryTableFilterRepository.isAscendingOrder,
                    limit,
                    offset
                )
        )
    }

    fun getQuery(limit: Int, offset: Int): Pair<String, List<Any>> {
        val queryNumber = getQueryNumber()

        val queryString = getQuerySelectPartCore(queryNumber).first +
                getQueryWhereMainPartCore(queryNumber).first +
                getQueryWhereAdditionalPartCore(queryNumber).first +
                getQueryGroupByPartCore(queryNumber).first +
                getQueryWindowPartCore(queryNumber).first +
                getQueryLastPart(limit, offset).first
        val args = getQuerySelectPartCore(queryNumber).second +
                getQueryWhereMainPartCore(queryNumber).second +
                getQueryWhereAdditionalPartCore(queryNumber).second +
                getQueryGroupByPartCore(queryNumber).second +
                getQueryWindowPartCore(queryNumber).second +
                getQueryLastPart(limit, offset).second

        return Pair(queryString, args)
    }

    private fun List<*>.toPlaceholders(): String =
        if (this.isEmpty()) "NULL" else joinToString(separator = ",") { "?" }

}
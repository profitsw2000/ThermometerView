package ru.profitsw2000.data.room.utils

import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod

private const val SELECT_KEY_WORD = "SELECT "
private const val FROM_KEY_WORD = "FROM SensorHistoryDataEntity "
private const val WHERE_KEY_WORD = "WHERE "
private const val AND_KEY_WORD = "AND "
private const val GROUP_KEY_WORD = "GROUP "
private const val ORDER_KEY_WORD = "ORDER "
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
    private fun getQuerySelectPartCore(): Pair<String, List<Any>> {
        val queryNumber = getQueryNumber()
        val pseudonymsStringQuery = "MIN(id) AS id, sensorId, localId, letterCode, MIN(date) AS date"
        val pseudonymsStringWindowQuery = "MIN(date) OVER w AS date, id, sensorId, localId, letterCode"
        val fromEntityStringQuery = "FROM SensorHistoryDataEntity"

        return when(queryNumber) {
            FIRST_QUERY, SECOND_QUERY ->
                Pair(
                    "SELECT *",
                    listOf()
                )
            THIRD_QUERY, SEVENTH_QUERY ->
                Pair(
                    "SELECT AVG(temperature) AS temperature, $pseudonymsStringQuery $fromEntityStringQuery",
                    listOf()
                )
            FOURTH_QUERY, EIGHTH_QUERY ->
                Pair(
                    "SELECT MAX(temperature) AS temperature, $pseudonymsStringQuery $fromEntityStringQuery",
                    listOf()
                )
            FIFTH_QUERY, NINTH_QUERY ->
                Pair(
                    "SELECT MIN(temperature) AS temperature, $pseudonymsStringQuery $fromEntityStringQuery",
                    listOf()
                )
            SIXTH_QUERY, TENTH_QUERY, ELEVENTH_QUERY, TWELVE_QUERY ->
                Pair(
                    "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, $pseudonymsStringWindowQuery $fromEntityStringQuery",
                    listOf()
                )
            else -> Pair("SELECT *", listOf())
        }
    }

    private fun getQueryWhereMainPartCore(): Pair<String, List<Any>> {
        val queryNumber = getQueryNumber()

        return when(queryNumber) {
            SECOND_QUERY, THIRD_QUERY, FOURTH_QUERY, FIFTH_QUERY, SIXTH_QUERY, ELEVENTH_QUERY ->
                Pair(
                    first = "WHERE (sensorId IN ?  OR localId IN ?  OR letterCode IN ?)",
                    second = listOf(
                            sensorHistoryTableFilterRepository.sensorIdList,
                            sensorHistoryTableFilterRepository.localIdList,
                            sensorHistoryTableFilterRepository.letterCodeList)
                )
            else -> Pair(
                first = "WHERE date BETWEEN ? AND ?",
                second = listOf(
                    sensorHistoryTableFilterRepository.fromDate ?: Long.MIN_VALUE,
                    sensorHistoryTableFilterRepository.toDate ?: Long.MAX_VALUE)
            )
        }
    }

    private fun getQueryWhereAdditionalPartCore(): Pair<String, List<Any>> {
        val queryNumber = getQueryNumber()

        return when(queryNumber) {
            SECOND_QUERY, THIRD_QUERY, FOURTH_QUERY, FIFTH_QUERY, SIXTH_QUERY, ELEVENTH_QUERY ->
                Pair(
                    first = "AND date BETWEEN ? AND ?",
                    second = listOf(
                        sensorHistoryTableFilterRepository.fromDate ?: Long.MIN_VALUE,
                        sensorHistoryTableFilterRepository.toDate ?: Long.MAX_VALUE
                    )
                )
            else -> Pair(first = "", second = listOf())
        }
    }

    private fun getQueryGroupByPartCore(): Pair<String, List<Any>> {
        val queryNumber = getQueryNumber()

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

    private fun getQueryWindowPartCore(): Pair<String, List<Any>> {
        val queryNumber = getQueryNumber()

        return when(queryNumber) {
            SIXTH_QUERY, TENTH_QUERY ->
                Pair(
                    first = "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY " +
                            "CASE WHEN ? THEN SensorHistoryDataEntity.date END DESC, " +
                            "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END ASC)",
                    second = listOf(
                        sensorHistoryTableFilterRepository.timeFrameMillis,
                        true,
                        true
                    )
                )
            ELEVENTH_QUERY, TWELVE_QUERY ->
                Pair(
                    first = "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY " +
                            "CASE WHEN ? THEN SensorHistoryDataEntity.date END DESC, " +
                            "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END ASC)",
                    second = listOf(
                        sensorHistoryTableFilterRepository.timeFrameMillis,
                        false,
                        false
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
                   "LIMIT ? OFFSET ? ",
            listOf(
                    sensorHistoryTableFilterRepository.isAscendingOrder,
                    sensorHistoryTableFilterRepository.isAscendingOrder,
                    limit,
                    offset
                )
        )
    }

    fun getQuery(limit: Int, offset: Int): Pair<String, List<Any>> {
        val queryString = "${getQuerySelectPartCore().first} " +
                "${getQueryWhereMainPartCore().first} " +
                "${getQueryWhereAdditionalPartCore().first} " +
                "${getQueryGroupByPartCore().first} " +
                "${getQueryWindowPartCore().first} " +
                "${getQueryLastPart(limit, offset)}"
        val args = getQueryWindowPartCore().second +
                getQueryWhereMainPartCore().second +
                getQueryWhereAdditionalPartCore().second +
                getQueryGroupByPartCore().second +
                getQueryWindowPartCore().second +
                getQueryLastPart(limit, offset).second

        return Pair(queryString, args)
    }

}
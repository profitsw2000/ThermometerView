package ru.profitsw2000.data

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.profitsw2000.core.utils.constants.FOUR_HOURS_FRAME_MILLIS
import ru.profitsw2000.data.data.filter.SensorHistoryGraphFilterRepositoryImpl
import ru.profitsw2000.data.data.filter.SensorHistoryTableFilterRepositoryImpl
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import ru.profitsw2000.data.room.utils.SensorHistoryGraphQueryBuilder
import ru.profitsw2000.data.room.utils.SensorHistoryTableQueryBuilder

class SensorHistoryDataGraphQueryBuilderTest {
    private val filter = SensorHistoryGraphFilterRepositoryImpl()
    private val sensorIdList = listOf(5L, 10L, 15L)
    private val letterCodeList = listOf(1000, 2000, 3000)
    private val LIMIT = 48
    private val OFFSET = 0

    @Test
    fun firstQueryFirstCurveTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(23L, Long.MIN_VALUE, Long.MAX_VALUE, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = listOf(23L, 45L)

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getFirstCurveQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getFirstCurveQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun firstQuerySubsequentCurveTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(45L, Long.MIN_VALUE, Long.MAX_VALUE)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = listOf(23L, 45L)

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(1).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(1).second)
    }

    @Test
    fun firstQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0L, Long.MIN_VALUE, Long.MAX_VALUE)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(4).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(4).second)
    }

    @Test
    fun secondQueryFirstCurveTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(sensorIdList[0], Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getFirstCurveQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getFirstCurveQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun secondQuerySubsequentCurveTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(sensorIdList[2], Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(2).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(2).second)
    }

    @Test
    fun secondQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0L, Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(4).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(4).second)
    }
}
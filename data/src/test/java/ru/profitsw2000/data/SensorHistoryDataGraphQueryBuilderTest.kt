package ru.profitsw2000.data

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.profitsw2000.core.utils.constants.EIGHT_HOURS_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.FOUR_HOURS_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.ONE_HOUR_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.THIRTY_MINUTES_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.TWO_HOURS_FRAME_MILLIS
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

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
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
    fun firstQuerySubsequentCurveErrorNoDataTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)

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

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
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

    @Test
    fun secondQuerySubsequentCurveNoDataErrorTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(2).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(2).second)
    }

    @Test
    fun thirdQueryFirstCurveTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(sensorIdList[0], Long.MIN_VALUE, Long.MAX_VALUE,
            THIRTY_MINUTES_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = THIRTY_MINUTES_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun thirdQuerySubsequentCurveTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(sensorIdList[1], Long.MIN_VALUE, Long.MAX_VALUE, ONE_HOUR_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = ONE_HOUR_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(1).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(1).second)
    }

    @Test
    fun thirdQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
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
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(4).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(4).second)
    }

    @Test
    fun thirdQueryFirstCurveNoDataErrorTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE,
            THIRTY_MINUTES_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.timeFrameMillis = THIRTY_MINUTES_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fourthQueryFirstCurveTest() {
        val queryString = "SELECT MIN(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(sensorIdList[0], Long.MIN_VALUE, Long.MAX_VALUE,
            TWO_HOURS_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = TWO_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMinimum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fourthQuerySubsequentCurveTest() {
        val queryString = "SELECT MIN(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(sensorIdList[1], Long.MIN_VALUE, Long.MAX_VALUE, ONE_HOUR_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = ONE_HOUR_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMinimum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(1).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(1).second)
    }

    @Test
    fun fourthQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT MIN(temperature) AS temperature, " +
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
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMinimum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(4).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(4).second)
    }

    @Test
    fun fourthQuerySubsequentCurveNoDataErrorTest() {
        val queryString = "SELECT MIN(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMinimum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(1).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(1).second)
    }

    @Test
    fun fifthQueryFirstCurveTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date ASC) " +
                "ORDER BY date DESC" +
                " LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(sensorIdList[0], Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameBegin

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fifthQuerySubsequentCurveTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date ASC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(sensorIdList[2], Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameBegin

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(2).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(2).second)
    }

    @Test
    fun fifthQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date ASC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0L, Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameBegin

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(10).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(10).second)
    }

    @Test
    fun fifthQueryFirstCurveNoDataErrorTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date ASC) " +
                "ORDER BY date DESC" +
                " LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
       filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameBegin

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun sixthQueryFirstCurveTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date DESC) " +
                "ORDER BY date DESC" +
                " LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(sensorIdList[0], Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun sixthQuerySubsequentCurveTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date DESC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(sensorIdList[2], Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(2).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(2).second)
    }

    @Test
    fun sixthQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE sensorId LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date DESC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0L, Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(10).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(10).second)
    }

    @Test
    fun sixthQuerySubsequentCurveNoDataErrorTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date DESC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(2).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(2).second)
    }

    ///////03.03.2026   16:11


    @Test
    fun seventhQueryFirstCurveTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(23, Long.MIN_VALUE, Long.MAX_VALUE, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = listOf(23, 32)

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun seventhQuerySubsequentCurveTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(45, Long.MIN_VALUE, Long.MAX_VALUE)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = listOf(23, 45)

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(1).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(1).second)
    }

    @Test
    fun seventhQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(4).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(4).second)
    }

    @Test
    fun seventhQueryFirstCurveNoDataErrorTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun eighthQueryFirstCurveTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(letterCodeList[0], Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun eighthQuerySubsequentCurveTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(letterCodeList[2], Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(2).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(2).second)
    }

    @Test
    fun eighthQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(4).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(4).second)
    }

    @Test
    fun eighthQuerySubsequentCurveNoDataErrorTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(2).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(2).second)
    }

    @Test
    fun ninthQueryFirstCurveTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(letterCodeList[0], Long.MIN_VALUE, Long.MAX_VALUE,
            THIRTY_MINUTES_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = THIRTY_MINUTES_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun ninthQuerySubsequentCurveTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(letterCodeList[1], Long.MIN_VALUE, Long.MAX_VALUE, ONE_HOUR_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = ONE_HOUR_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(1).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(1).second)
    }

    @Test
    fun ninthQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(4).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(4).second)
    }

    @Test
    fun ninthQuerySubsequentCurveNoDataErrorTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(1).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(1).second)
    }

    @Test
    fun tenthQueryFirstCurveTest() {
        val queryString = "SELECT MIN(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(letterCodeList[0], Long.MIN_VALUE, Long.MAX_VALUE,
            TWO_HOURS_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = TWO_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMinimum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    ///03.03.2026  16:44
    @Test
    fun tenthQuerySubsequentCurveTest() {
        val queryString = "SELECT MIN(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(letterCodeList[1], Long.MIN_VALUE, Long.MAX_VALUE, ONE_HOUR_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = ONE_HOUR_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMinimum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(1).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(1).second)
    }

    @Test
    fun tenthQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT MIN(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE, FOUR_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMinimum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(4).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(4).second)
    }

    @Test
    fun tenthQueryFirstCurveNoDataErrorTest() {
        val queryString = "SELECT MIN(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY date DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE,
            TWO_HOURS_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.timeFrameMillis = TWO_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMinimum

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun eleventhQueryFirstCurveTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date ASC) " +
                "ORDER BY date DESC" +
                " LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(letterCodeList[0], Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameBegin

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun eleventhQuerySubsequentCurveTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date ASC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(letterCodeList[2], Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameBegin

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(2).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(2).second)
    }

    @Test
    fun eleventhQuerySubsequentCurveNoDataErrorTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date ASC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameBegin

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(2).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(2).second)
    }

    @Test
    fun eleventhQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date ASC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameBegin

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(10).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(10).second)
    }

    @Test
    fun twelveQueryFirstCurveTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date DESC) " +
                "ORDER BY date DESC" +
                " LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(letterCodeList[0], Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS, LIMIT, OFFSET)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun twelveQuerySubsequentCurveTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date DESC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(letterCodeList[2], Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(2).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(2).second)
    }

    @Test
    fun twelveQuerySubsequentCurveErrorTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date DESC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(10).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(10).second)
    }

    @Test
    fun twelveQuerySubsequentCurveNoDataErrorTest() {
        val queryString = "SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
                "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
                "FROM SensorHistoryDataEntity " +
                "WHERE letterCode LIKE ? " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "WINDOW w AS (PARTITION BY sensorId, date/? ORDER BY date DESC) " +
                "ORDER BY date DESC"
        val queryArgs = listOf<Any>(0, Long.MIN_VALUE, Long.MAX_VALUE,
            EIGHT_HOURS_FRAME_MILLIS, EIGHT_HOURS_FRAME_MILLIS)
        val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(filter)
        filter.timeFrameMillis = EIGHT_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameEnd

        assertEquals(queryString, sensorHistoryGraphQueryBuilder.getQuery(1).first)
        assertEquals(queryArgs, sensorHistoryGraphQueryBuilder.getQuery(1).second)
    }
}
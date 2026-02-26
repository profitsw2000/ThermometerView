package ru.profitsw2000.data

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import ru.profitsw2000.core.utils.constants.EIGHT_HOURS_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.FOUR_HOURS_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.ONE_DAY_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.ONE_WEEK_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.THIRTY_MINUTES_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.TWELVE_HOURS_FRAME_MILLIS
import ru.profitsw2000.data.data.filter.SensorHistoryTableFilterRepositoryImpl
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import ru.profitsw2000.data.room.utils.SensorHistoryTableQueryBuilder

class SensorHistoryDataTableQueryBuilderTest {
    private val filter = SensorHistoryTableFilterRepositoryImpl()
    private val sensorIdList = listOf(5L, 10L, 15L)
    private val localIdList = listOf(50, 100, 150)
    private val letterCodeList = listOf(1000, 2000, 3000)
    private val LIMIT = 48
    private val OFFSET = 0

    @Test
    fun firstQueryDescTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(Long.MIN_VALUE, Long.MAX_VALUE, false, false, LIMIT, OFFSET)
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun firstQueryAscTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(Long.MIN_VALUE, Long.MAX_VALUE, true, true, LIMIT, OFFSET)
        filter.isAscendingOrder = true
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun secondAQueryDescTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            listOf<Int>(),
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.sensorIdList = sensorIdList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun secondBQueryDescTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            localIdList,
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.localIdList = localIdList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun secondCQueryDescTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            listOf<Int>(),
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.letterCodeList = letterCodeList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun secondDQueryDescTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            localIdList,
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.sensorIdList = sensorIdList
        filter.localIdList = localIdList
        filter.letterCodeList = letterCodeList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun secondAQueryAscTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            listOf<Int>(),
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.sensorIdList = sensorIdList
        filter.isAscendingOrder = true
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun secondBQueryAscTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            localIdList,
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.isAscendingOrder = true
        filter.localIdList = localIdList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun secondCQueryAscTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            listOf<Int>(),
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.isAscendingOrder = true
        filter.letterCodeList = letterCodeList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun secondDQueryAscTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            localIdList,
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.isAscendingOrder = true
        filter.sensorIdList = sensorIdList
        filter.localIdList = localIdList
        filter.letterCodeList = letterCodeList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun thirdAQueryDescTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            listOf<Int>(),
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            THIRTY_MINUTES_FRAME_MILLIS,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = THIRTY_MINUTES_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun thirdBQueryDescTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            localIdList,
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            FOUR_HOURS_FRAME_MILLIS,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.localIdList = localIdList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun thirdCQueryDescTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            listOf<Int>(),
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            FOUR_HOURS_FRAME_MILLIS,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun thirdDQueryDescTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            localIdList,
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            TWELVE_HOURS_FRAME_MILLIS,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.sensorIdList = sensorIdList
        filter.localIdList = localIdList
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = TWELVE_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun thirdAQueryAscTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            listOf<Int>(),
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            THIRTY_MINUTES_FRAME_MILLIS,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = THIRTY_MINUTES_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage
        filter.isAscendingOrder = true
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun thirdBQueryAscTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            localIdList,
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            ONE_WEEK_FRAME_MILLIS,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.isAscendingOrder = true
        filter.timeFrameMillis = ONE_WEEK_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage
        filter.localIdList = localIdList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun thirdCQueryAscTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            listOf<Int>(),
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            THIRTY_MINUTES_FRAME_MILLIS,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.isAscendingOrder = true
        filter.timeFrameMillis = THIRTY_MINUTES_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage
        filter.letterCodeList = letterCodeList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun thirdDQueryAscTest() {
        val queryString = "SELECT AVG(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            localIdList,
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            ONE_DAY_FRAME_MILLIS,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.isAscendingOrder = true
        filter.timeFrameMillis = ONE_DAY_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameAverage
        filter.sensorIdList = sensorIdList
        filter.localIdList = localIdList
        filter.letterCodeList = letterCodeList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fourthAQueryDescTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            listOf<Int>(),
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            THIRTY_MINUTES_FRAME_MILLIS,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = THIRTY_MINUTES_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fourthBQueryDescTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            localIdList,
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            FOUR_HOURS_FRAME_MILLIS,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.localIdList = localIdList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fourthCQueryDescTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            listOf<Int>(),
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            FOUR_HOURS_FRAME_MILLIS,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = FOUR_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fourthDQueryDescTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            localIdList,
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            TWELVE_HOURS_FRAME_MILLIS,
            false,
            false,
            LIMIT,
            OFFSET
        )
        filter.sensorIdList = sensorIdList
        filter.localIdList = localIdList
        filter.letterCodeList = letterCodeList
        filter.timeFrameMillis = TWELVE_HOURS_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fourthAQueryAscTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            listOf<Int>(),
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            THIRTY_MINUTES_FRAME_MILLIS,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.sensorIdList = sensorIdList
        filter.timeFrameMillis = THIRTY_MINUTES_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum
        filter.isAscendingOrder = true
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fourthBQueryAscTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            localIdList,
            listOf<Int>(),
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            ONE_WEEK_FRAME_MILLIS,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.isAscendingOrder = true
        filter.timeFrameMillis = ONE_WEEK_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum
        filter.localIdList = localIdList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fourthCQueryAscTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            listOf<Int>(),
            listOf<Int>(),
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            THIRTY_MINUTES_FRAME_MILLIS,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.isAscendingOrder = true
        filter.timeFrameMillis = THIRTY_MINUTES_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum
        filter.letterCodeList = letterCodeList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }

    @Test
    fun fourthDQueryAscTest() {
        val queryString = "SELECT MAX(temperature) AS temperature, " +
                "MIN(id) AS id, " +
                "sensorId, localId, letterCode, MIN(date) AS date " +
                "FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY sensorId, date/? " +
                "ORDER BY " +
                "CASE WHEN ? THEN date END ASC, " +
                "CASE WHEN NOT ? THEN date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(
            sensorIdList,
            localIdList,
            letterCodeList,
            Long.MIN_VALUE,
            Long.MAX_VALUE,
            ONE_DAY_FRAME_MILLIS,
            true,
            true,
            LIMIT,
            OFFSET
        )
        filter.isAscendingOrder = true
        filter.timeFrameMillis = ONE_DAY_FRAME_MILLIS
        filter.timeFrameDataObtainingMethod = TimeFrameDataObtainingMethod.TimeFrameMaximum
        filter.sensorIdList = sensorIdList
        filter.localIdList = localIdList
        filter.letterCodeList = letterCodeList
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertEquals(queryString, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).first)
        assertEquals(queryArgs, sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).second)
    }
}
package ru.profitsw2000.data

import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import ru.profitsw2000.data.data.filter.SensorHistoryTableFilterRepositoryImpl
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
                "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
                "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(Long.MIN_VALUE, Long.MAX_VALUE, false, false, LIMIT, OFFSET)
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertTrue(
            sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).equals(
                Pair(queryString, queryArgs)
            )
        )
    }

    @Test
    fun firstQueryAscTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE date BETWEEN ? AND ? " +
                "ORDER BY " +
                "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
                "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC " +
                "LIMIT ? OFFSET ?"
        val queryArgs = listOf<Any>(Long.MIN_VALUE, Long.MAX_VALUE, true, true, LIMIT, OFFSET)
        filter.isAscendingOrder = true
        val sensorHistoryTableQueryBuilder = SensorHistoryTableQueryBuilder(filter)

        assertTrue(
            sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).equals(
                Pair(queryString, queryArgs)
            )
        )
    }

    @Test
    fun secondAQueryDescTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ?" +
                "ORDER BY " +
                "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
                "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC " +
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

        assertTrue(
            sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).equals(
                Pair(queryString, queryArgs)
            )
        )
    }

    @Test
    fun secondBQueryDescTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ?" +
                "ORDER BY " +
                "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
                "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC " +
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

        assertTrue(
            sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).equals(
                Pair(queryString, queryArgs)
            )
        )
    }

    @Test
    fun secondCQueryDescTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ?" +
                "ORDER BY " +
                "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
                "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC " +
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

        assertTrue(
            sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).equals(
                Pair(queryString, queryArgs)
            )
        )
    }

    @Test
    fun secondDQueryDescTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ?" +
                "ORDER BY " +
                "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
                "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC " +
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

        assertTrue(
            sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).equals(
                Pair(queryString, queryArgs)
            )
        )
    }

    @Test
    fun secondAQueryAscTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ?" +
                "ORDER BY " +
                "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
                "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC " +
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

        assertTrue(
            sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).equals(
                Pair(queryString, queryArgs)
            )
        )
    }

    @Test
    fun secondBQueryAscTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ?" +
                "ORDER BY " +
                "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
                "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC " +
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

        assertTrue(
            sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).equals(
                Pair(queryString, queryArgs)
            )
        )
    }

    @Test
    fun secondCQueryAscTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ?" +
                "ORDER BY " +
                "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
                "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC " +
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

        assertTrue(
            sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).equals(
                Pair(queryString, queryArgs)
            )
        )
    }

    @Test
    fun secondDQueryAscTest() {
        val queryString = "SELECT * FROM SensorHistoryDataEntity " +
                "WHERE (sensorId IN ? " +
                "OR localId IN ? " +
                "OR letterCode IN ?) " +
                "AND date BETWEEN ? AND ?" +
                "ORDER BY " +
                "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
                "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC " +
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

        assertTrue(
            sensorHistoryTableQueryBuilder.getQuery(LIMIT, OFFSET).equals(
                Pair(queryString, queryArgs)
            )
        )
    }
}
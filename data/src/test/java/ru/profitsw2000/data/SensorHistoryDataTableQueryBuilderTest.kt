package ru.profitsw2000.data

import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import ru.profitsw2000.data.data.filter.SensorHistoryTableFilterRepositoryImpl
import ru.profitsw2000.data.room.utils.SensorHistoryTableQueryBuilder

class SensorHistoryDataTableQueryBuilderTest {
    private val filter = SensorHistoryTableFilterRepositoryImpl()
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
    fun secondQueryDescTest() {
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
}
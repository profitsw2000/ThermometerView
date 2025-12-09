package ru.profitsw2000.data.room.utils

import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository

class SensorHistoryTableQueryBuilder(
    sensorHistoryTableFilterRepository: SensorHistoryTableFilterRepository
) {

    private val fromPart = "FROM SensorHistoryDataEntity "
    private val dateRangePart = "WHERE date BETWEEN ? AND ? "
    private val fullListOrderByPart = "ORDER BY " +
            "CASE WHEN ? THEN SensorHistoryDataEntity.date END ASC, " +
            "CASE WHEN NOT ? THEN SensorHistoryDataEntity.date END DESC "
    private val endPart = "LIMIT ? OFFSET ?"


}
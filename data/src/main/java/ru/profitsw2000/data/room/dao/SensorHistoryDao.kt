package ru.profitsw2000.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity
import java.util.Date

@Dao
interface SensorHistoryDao {

    suspend fun getSensorHistoryList(
        filter: SensorHistoryTableFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return if (filter.sensorIdList.isEmpty()
            && filter.localIdList.isEmpty()
            && filter.letterCodeList.isEmpty()) {
            getSensorHistoryListWithoutMainFilters(
                filter = filter,
                limit = limit,
                offset = offset
            )
        }
        else getFilteredSensorHistoryList(
            filter = filter,
            limit = limit,
            offset = offset
        )
    }

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE date BETWEEN (:fromDate) AND (:toDate) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN SensorHistoryDataEntity.date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN SensorHistoryDataEntity.date END DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getUnfilteredSensorHistoryList(
        fromDate: Date,
        toDate: Date,
        orderIsAscending: Boolean,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN SensorHistoryDataEntity.date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN SensorHistoryDataEntity.date END DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getFilteredByMainFieldsSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT AVG(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate) " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getFilteredByMainFieldsAverageTemperatureGroupedByDateSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MAX(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getFilteredByMainFieldsMaxTemperatureGroupedByDateSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MIN(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getFilteredByMainFieldsMinTemperatureGroupedByDateSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY " +
                        "CASE WHEN (:isFirstValue) THEN SensorHistoryDataEntity.date END DESC, " +
                        "CASE WHEN NOT (:isFirstValue) THEN SensorHistoryDataEntity.date END ASC " +
            ")" +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getFilteredByMainFieldsTimeFrameBoundaryTemperatureValueSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        timeFrameInMillis: Long,
        isFirstValue: Boolean,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT AVG(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE date BETWEEN (:startDate) AND (:endDate) " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getAverageTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MAX(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getMaxTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MIN(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getMinTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY " +
            "           CASE WHEN (:isFirstValue) THEN SensorHistoryDataEntity.date END DESC, " +
            "           CASE WHEN NOT (:isFirstValue) THEN SensorHistoryDataEntity.date END ASC " +
            ")" +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getTimeFrameBoundaryTemperatureValueWithoutMainFiltersSensorHistoryList(
        timeFrameInMillis: Long,
        isFirstValue: Boolean,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    private suspend fun getSensorHistoryListWithoutMainFilters(
        filter: SensorHistoryTableFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return if (filter.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            getUnfilteredSensorHistoryList(
                fromDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                toDate = filter.toDate ?: Date(Long.MAX_VALUE),
                orderIsAscending = filter.isAscendingOrder,
                limit = limit,
                offset = offset
            )
        else
            getTimeFrameTemperatureValueWithoutMainFiltersSensorHistoryList(
                filter = filter,
                limit = limit,
                offset = offset
            )
    }

    private suspend fun getFilteredSensorHistoryList(
        filter: SensorHistoryTableFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return if (filter.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            getFilteredByMainFieldsSensorHistoryList(
                sensorIdList = filter.sensorIdList,
                localIdList = filter.localIdList,
                letterCodeList = filter.letterCodeList,
                orderIsAscending = filter.isAscendingOrder,
                startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                limit = limit,
                offset = offset
            )
        else
            getFilteredTimeFrameTemperatureValueSensorHistoryList(
                filter = filter,
                limit = limit,
                offset = offset
            )
    }

    private suspend fun getTimeFrameTemperatureValueWithoutMainFiltersSensorHistoryList(
        filter: SensorHistoryTableFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return when(filter.timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage ->
                getAverageTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameMaximum ->
                getMaxTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameMinimum ->
                getMinTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameBegin ->
                getTimeFrameBoundaryTemperatureValueWithoutMainFiltersSensorHistoryList(
                    timeFrameInMillis = filter.timeFrameMillis,
                    isFirstValue = true,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameEnd ->
                getTimeFrameBoundaryTemperatureValueWithoutMainFiltersSensorHistoryList(
                    timeFrameInMillis = filter.timeFrameMillis,
                    isFirstValue = false,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
        }
    }

    private suspend fun getFilteredTimeFrameTemperatureValueSensorHistoryList(
        filter: SensorHistoryTableFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return when(filter.timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage ->
                getFilteredByMainFieldsAverageTemperatureGroupedByDateSensorHistoryList(
                    sensorIdList = filter.sensorIdList,
                    localIdList = filter.localIdList,
                    letterCodeList = filter.letterCodeList,
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameMaximum ->
                getFilteredByMainFieldsMaxTemperatureGroupedByDateSensorHistoryList(
                    sensorIdList = filter.sensorIdList,
                    localIdList = filter.localIdList,
                    letterCodeList = filter.letterCodeList,
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameMinimum ->
                getFilteredByMainFieldsMinTemperatureGroupedByDateSensorHistoryList(
                    sensorIdList = filter.sensorIdList,
                    localIdList = filter.localIdList,
                    letterCodeList = filter.letterCodeList,
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameBegin ->
                getFilteredByMainFieldsTimeFrameBoundaryTemperatureValueSensorHistoryList(
                    sensorIdList = filter.sensorIdList,
                    localIdList = filter.localIdList,
                    letterCodeList = filter.letterCodeList,
                    timeFrameInMillis = filter.timeFrameMillis,
                    isFirstValue = true,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameEnd ->
                getFilteredByMainFieldsTimeFrameBoundaryTemperatureValueSensorHistoryList(
                    sensorIdList = filter.sensorIdList,
                    localIdList = filter.localIdList,
                    letterCodeList = filter.letterCodeList,
                    timeFrameInMillis = filter.timeFrameMillis,
                    isFirstValue = false,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
        }
    }

    @Query("SELECT DISTINCT sensorId FROM SensorHistoryDataEntity")
    suspend fun getAllSensorsIdList(): List<Long>

    @Query("SELECT DISTINCT localId FROM SensorHistoryDataEntity")
    suspend fun getAllSensorsLocalIdList(): List<Int>

    @Query("SELECT DISTINCT letterCode FROM SensorHistoryDataEntity")
    suspend fun getAllLetterCodesList(): List<Int>

    @Query("SELECT COUNT(*) FROM SensorHistoryDataEntity")
    suspend fun getSensorHistoryDataEntityCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sensorHistoryDataEntity: SensorHistoryDataEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>)
}
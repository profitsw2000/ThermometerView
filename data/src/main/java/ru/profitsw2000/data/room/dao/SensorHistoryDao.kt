package ru.profitsw2000.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity
import java.util.Date

@Dao
interface SensorHistoryDao {

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN SensorHistoryDataEntity.date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN SensorHistoryDataEntity.date END DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getUnfilteredSensorHistoryList(
        orderIsAscending: Boolean, limit: Int, offset: Int
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
            "MIN(id), " +
            "sensorId, localId, letterCode, " +
            "CASE WHEN (:orderIsAscending) THEN MIN(date) ELSE MAX(date) END as timeFrameDate " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate) " +
            "GROUP BY sensorId, date/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN timeFrameDate END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN timeFrameDate END DESC " +
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
            "MIN(id), " +
            "sensorId, localId, letterCode, " +
            "CASE WHEN (:orderIsAscending) THEN MIN(date) ELSE MAX(date) END as timeFrameDate " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, date/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN timeFrameDate END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN timeFrameDate END DESC " +
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
            "MIN(id), " +
            "sensorId, localId, letterCode, " +
            "CASE WHEN (:orderIsAscending) THEN MIN(date) ELSE MAX(date) END as timeFrameDate " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, date/(:timeFrameInMillis)" +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN timeFrameDate END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN timeFrameDate END DESC " +
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
            "FIRST_VALUE(date) OVER w AS timeFrameDate, * " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, date/(:timeFrameInMillis)" +
            "       ORDER BY " +
                        "CASE WHEN (:isFirstValue) THEN SensorHistoryDataEntity.date END DESC, " +
                        "CASE WHEN NOT (:isFirstValue) THEN SensorHistoryDataEntity.date END ASC " +
            ")" +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN timeFrameDate END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN timeFrameDate END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getFilteredByMainFieldsTimeFrameBoundryTemperatureValueSensorHistoryList(
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

    suspend fun getSensorHistoryList(sensorIdList: List<Long>,
                                     localIdList: List<Int>,
                                     letterCodeList: List<Int>,
                                     orderIsAscending: Boolean,
                                     limit: Int,
                                     offset: Int): List<SensorHistoryDataEntity> {
        return if (sensorIdList.isEmpty() && localIdList.isEmpty() && letterCodeList.isEmpty())
            getUnfilteredSensorHistoryList(orderIsAscending, limit, offset)
        else getUnfilteredSensorHistoryList(orderIsAscending, limit, offset)
/*        getFilteredSensorHistoryList(
            sensorIdList,
            localIdList,
            letterCodeList,
            orderIsAscending,
            limit,
            offset
        )*/
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
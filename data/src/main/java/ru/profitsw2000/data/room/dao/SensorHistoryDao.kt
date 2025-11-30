package ru.profitsw2000.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

@Dao
interface SensorHistoryDao {

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN SensorHistoryDataEntity.date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN SensorHistoryDataEntity.date END DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getUnfilteredSensorHistoryList(orderIsAscending: Boolean, limit: Int, offset: Int): List<SensorHistoryDataEntity>

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN SensorHistoryDataEntity.date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN SensorHistoryDataEntity.date END DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getFilteredSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        orderIsAscending: Boolean,
        limit: Int,
        offset: Int): List<SensorHistoryDataEntity>

    suspend fun getSensorHistoryList(sensorIdList: List<Long>,
                                     localIdList: List<Int>,
                                     letterCodeList: List<Int>,
                                     orderIsAscending: Boolean,
                                     limit: Int,
                                     offset: Int): List<SensorHistoryDataEntity> {
        return if (sensorIdList.isEmpty() && localIdList.isEmpty() && letterCodeList.isEmpty())
            getUnfilteredSensorHistoryList(orderIsAscending, limit, offset)
        else getFilteredSensorHistoryList(
            sensorIdList,
            localIdList,
            letterCodeList,
            orderIsAscending,
            limit,
            offset
        )
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
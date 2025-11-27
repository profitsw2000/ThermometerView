package ru.profitsw2000.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

@Dao
interface SensorHistoryDao {

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "ORDER BY SensorHistoryDataEntity.date DESC LIMIT :limit OFFSET :offset")
    suspend fun getSensorHistoryList(limit: Int, offset: Int): List<SensorHistoryDataEntity>

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE (:sensorIdList IS NULL OR :sensorIdList = '') OR sensorId IN (:sensorIdList)")
    suspend fun getFilteredSensorHistoryList(sensorIdList: List<Long>)

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
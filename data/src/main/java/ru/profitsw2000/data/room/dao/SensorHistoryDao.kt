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
    suspend fun getSensorHistoryList(limit: Int, offset: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sensorHistoryDataEntity: SensorHistoryDataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>)
}
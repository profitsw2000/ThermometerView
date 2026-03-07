package ru.profitsw2000.data.room.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity
import java.util.Date

@Dao
interface SensorHistoryDao {

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE SensorHistoryDataEntity.sensorId LIKE :sensorId " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getSimpleSensorHistoryList(sensorId: Long, limit: Int, offset: Int): List<SensorHistoryDataEntity>

    @RawQuery
    suspend fun getSqlSensorHistoryList(query: SupportSQLiteQuery): List<SensorHistoryDataEntity>

    @RawQuery
    suspend fun getSqlSensorHistoryListCount(query: SupportSQLiteQuery): Int

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
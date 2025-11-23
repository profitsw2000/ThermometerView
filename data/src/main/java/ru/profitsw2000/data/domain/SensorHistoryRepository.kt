package ru.profitsw2000.data.domain

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

interface SensorHistoryRepository {

    suspend fun writeHistoryItem(sensorHistoryDataEntity: SensorHistoryDataEntity)

    suspend fun writeHistoryItemList(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>)

    fun getHistoryPagedData(): Flow<PagingData<SensorHistoryDataModel>>

    suspend fun getAllSensorIds(): List<Long>

    suspend fun getAllSensorLocalIds(): List<Int>

    suspend fun getAllLetterCodes(): List<Int>

    suspend fun getHistoryDataEntitySize(): Int

}
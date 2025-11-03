package ru.profitsw2000.data.domain

import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

interface SensorHistoryRepository {

    suspend fun writeHistoryItem(sensorHistoryDataEntity: SensorHistoryDataEntity)

    suspend fun writeHistoryItemList(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>)

}
package ru.profitsw2000.data.data.local

import ru.profitsw2000.data.domain.local.SensorHistoryRepositoryLocal
import ru.profitsw2000.data.room.database.AppDatabase
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

class SensorHistoryRepositoryLocalImpl(
    private val database: AppDatabase
): SensorHistoryRepositoryLocal {

    override suspend fun writeHistoryItem(sensorHistoryDataEntity: SensorHistoryDataEntity) {
        database.sensorHistoryDao.insert(sensorHistoryDataEntity=sensorHistoryDataEntity)
    }

    override suspend fun writeHistoryItemList(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>) {
        database.sensorHistoryDao.insertList(sensorHistoryDataEntityList = sensorHistoryDataEntityList)
    }
}
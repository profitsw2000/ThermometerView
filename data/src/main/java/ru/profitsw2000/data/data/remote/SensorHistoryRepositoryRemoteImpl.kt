package ru.profitsw2000.data.data.remote

import ru.profitsw2000.data.domain.remote.SensorHistoryRepositoryRemote
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

class SensorHistoryRepositoryRemoteImpl: SensorHistoryRepositoryRemote {
    override suspend fun writeHistoryItem(sensorHistoryDataEntity: SensorHistoryDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun writeHistoryItemList(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>) {
        TODO("Not yet implemented")
    }
}
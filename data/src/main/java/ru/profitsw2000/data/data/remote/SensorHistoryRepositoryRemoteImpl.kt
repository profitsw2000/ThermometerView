package ru.profitsw2000.data.data.remote

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.profitsw2000.data.domain.remote.SensorHistoryRepositoryRemote
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

class SensorHistoryRepositoryRemoteImpl: SensorHistoryRepositoryRemote {
    override suspend fun writeHistoryItem(sensorHistoryDataEntity: SensorHistoryDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun writeHistoryItemList(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>) {
        TODO("Not yet implemented")
    }

    override fun getHistoryPagedData(): Flow<PagingData<SensorHistoryDataModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSensorIds(): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSensorLocalIds(): List<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllLetterCodes(): List<Int> {
        TODO("Not yet implemented")
    }
}
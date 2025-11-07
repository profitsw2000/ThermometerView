package ru.profitsw2000.data.data.local

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import ru.profitsw2000.data.data.local.source.HistoryListPagingSource
import ru.profitsw2000.data.domain.local.SensorHistoryRepositoryLocal
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.room.database.AppDatabase
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

class SensorHistoryRepositoryLocalImpl(
    private val database: AppDatabase,
    private val historyListPagingSource: HistoryListPagingSource
): SensorHistoryRepositoryLocal {

    override suspend fun writeHistoryItem(sensorHistoryDataEntity: SensorHistoryDataEntity) {
        database.sensorHistoryDao.insert(sensorHistoryDataEntity=sensorHistoryDataEntity)
    }

    override suspend fun writeHistoryItemList(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>) {
        database.sensorHistoryDao.insertList(sensorHistoryDataEntityList = sensorHistoryDataEntityList)
    }

    override fun getHistoryPagedData(): Flow<PagingData<SensorHistoryDataModel>> {
        return Pager(
            PagingConfig(
                pageSize = 50,
                enablePlaceholders = false,
                initialLoadSize = 100
            )
        ) {
            historyListPagingSource
        }.flow
    }
}
package ru.profitsw2000.data.data.local

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.liveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.data.data.local.source.HistoryListPagingSource
import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.domain.local.SensorHistoryRepositoryLocal
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.room.database.AppDatabase
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

class SensorHistoryRepositoryLocalImpl(
    private val database: AppDatabase,
    private val sensorHistoryMapper: SensorHistoryMapper,
    private val sensorHistoryTableFilterRepository: SensorHistoryTableFilterRepository
): SensorHistoryRepositoryLocal {

    private var currentPagingSource: HistoryListPagingSource? = null

    override suspend fun writeHistoryItem(sensorHistoryDataEntity: SensorHistoryDataEntity) {
        database.sensorHistoryDao.insert(sensorHistoryDataEntity=sensorHistoryDataEntity)
        currentPagingSource?.invalidate()
    }

    override suspend fun writeHistoryItemList(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>) {
        database.sensorHistoryDao.insertList(sensorHistoryDataEntityList = sensorHistoryDataEntityList)
        currentPagingSource?.invalidate()
    }

    override fun getHistoryPagedData(): Flow<PagingData<SensorHistoryDataModel>> {
        return Pager(
            PagingConfig(
                pageSize = 15120,
                enablePlaceholders = false,
                initialLoadSize = 15120
            ),
            pagingSourceFactory = { HistoryListPagingSource(
                database = database,
                sensorHistoryMapper = sensorHistoryMapper,
                sensorHistoryTableFilterRepository = sensorHistoryTableFilterRepository).also {
                    currentPagingSource = it
                }
            }
        ).flow
    }

    override suspend fun getAllSensorIds(): List<Long> {
        return database.sensorHistoryDao.getAllSensorsIdList()
    }

    override suspend fun getAllSensorLocalIds(): List<Int> {
        return database.sensorHistoryDao.getAllSensorsLocalIdList()
    }

    override suspend fun getAllLetterCodes(): List<Int> {
        return database.sensorHistoryDao.getAllLetterCodesList()
    }

    override suspend fun getHistoryDataEntitySize(): Int {
        return database.sensorHistoryDao.getSensorHistoryDataEntityCount()
    }

    override fun invalidateDataSource() {
        currentPagingSource?.invalidate()
    }
}
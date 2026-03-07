package ru.profitsw2000.data.data.local

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.liveData
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.data.data.local.source.HistoryListPagingSource
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.domain.local.SensorHistoryRepositoryLocal
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.room.database.AppDatabase
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity
import ru.profitsw2000.data.room.utils.SensorHistoryGraphQueryBuilder
import java.util.Date

class SensorHistoryRepositoryLocalImpl(
    private val database: AppDatabase,
    private val sensorHistoryMapper: SensorHistoryMapper,
    private val sensorHistoryTableFilterRepository: SensorHistoryTableFilterRepository,
    private val sensorHistoryGraphFilterRepository: SensorHistoryGraphFilterRepository
): SensorHistoryRepositoryLocal {

    private var currentPagingSource: HistoryListPagingSource? = null
    private val sensorHistoryGraphQueryBuilder = SensorHistoryGraphQueryBuilder(sensorHistoryGraphFilterRepository)

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
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
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

    override suspend fun getSimpleSensorHistoryList(
        sensorId: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return database.sensorHistoryDao.getSimpleSensorHistoryList(sensorId, limit, offset)
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

    override suspend fun getGraphFirstCurveSensorHistoryList(
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return database.sensorHistoryDao.getSqlSensorHistoryList(getSqliteQuery(limit, offset))//getGraphFirstCurveSensorHistoryList(filter, limit, offset)
    }

    override suspend fun getGraphSensorHistoryListCount(): Int {
        return database.sensorHistoryDao.getSqlSensorHistoryListCount(getSqliteQueryForCount())
    }

    override suspend fun getGraphSubsequentCurvesSensorHistoryList(
        sensorIndex: Int,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity> {
        return database.sensorHistoryDao.getSqlSensorHistoryList(getSqliteQuery(sensorIndex, fromDate, toDate))//getGraphSubsequentCurvesSensorHistoryList(sensorIndex, filter, fromDate, toDate)
    }

    override fun invalidateDataSource() {
        currentPagingSource?.invalidate()
    }

    private fun getSqliteQueryForCount(): SimpleSQLiteQuery {
        val queryPair = sensorHistoryGraphQueryBuilder.getCountQuery()

        return SimpleSQLiteQuery(queryPair.first, queryPair.second.toTypedArray())
    }

    private fun getSqliteQuery(limit: Int, offset: Int): SimpleSQLiteQuery {
        val queryPair = sensorHistoryGraphQueryBuilder.getQuery(limit, offset)

        return SimpleSQLiteQuery(queryPair.first, queryPair.second.toTypedArray())
    }

    private fun getSqliteQuery(sensorIndex: Int, fromDate: Date, toDate: Date): SimpleSQLiteQuery {
        val queryPair = sensorHistoryGraphQueryBuilder.getQuery(sensorIndex, fromDate, toDate)

        return SimpleSQLiteQuery(queryPair.first, queryPair.second.toTypedArray())
    }
}
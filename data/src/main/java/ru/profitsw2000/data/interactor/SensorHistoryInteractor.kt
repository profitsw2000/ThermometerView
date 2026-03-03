package ru.profitsw2000.data.interactor

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.data.domain.local.SensorHistoryRepositoryLocal
import ru.profitsw2000.data.domain.remote.SensorHistoryRepositoryRemote
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity
import java.util.Date

class SensorHistoryInteractor(
    val sensorHistoryRepositoryLocal: SensorHistoryRepositoryLocal,
    val sensorHistoryRepositoryRemote: SensorHistoryRepositoryRemote
) {

    suspend fun writeHistoryItem(
        sensorHistoryDataEntity: SensorHistoryDataEntity,
        isRemote: Boolean
    ) {
        if (isRemote) sensorHistoryRepositoryRemote.writeHistoryItem(sensorHistoryDataEntity = sensorHistoryDataEntity)
        else sensorHistoryRepositoryLocal.writeHistoryItem(sensorHistoryDataEntity = sensorHistoryDataEntity)
    }

    suspend fun writeHistoryItemList(
        sensorHistoryDataEntityList: List<SensorHistoryDataEntity>,
        isRemote: Boolean
    ) {
        if (isRemote) sensorHistoryRepositoryRemote.writeHistoryItemList(sensorHistoryDataEntityList = sensorHistoryDataEntityList)
        else sensorHistoryRepositoryLocal.writeHistoryItemList(sensorHistoryDataEntityList = sensorHistoryDataEntityList)
    }

    suspend fun getSimpleSensorHistoryList(sensorId: Long, limit: Int, offset: Int, isRemote: Boolean): List<SensorHistoryDataEntity> {
        return if (isRemote) sensorHistoryRepositoryRemote.getSimpleSensorHistoryList(sensorId, limit, offset)
        else sensorHistoryRepositoryLocal.getSimpleSensorHistoryList(sensorId, limit, offset)
    }

    fun getHistoryPagedData(isRemote: Boolean): Flow<PagingData<SensorHistoryDataModel>> {
        return if (isRemote) sensorHistoryRepositoryRemote.getHistoryPagedData()
        else sensorHistoryRepositoryLocal.getHistoryPagedData()
    }

    suspend fun getAllSensorIds(isRemote: Boolean): List<Long> {
        return if (isRemote) sensorHistoryRepositoryRemote.getAllSensorIds()
        else sensorHistoryRepositoryLocal.getAllSensorIds()
    }

    suspend fun getAllSensorLocalIds(isRemote: Boolean): List<Int> {
        return if (isRemote) sensorHistoryRepositoryRemote.getAllSensorLocalIds()
        else sensorHistoryRepositoryLocal.getAllSensorLocalIds()
    }

    suspend fun getAllLetterCodes(isRemote: Boolean): List<Int> {
        return if (isRemote) sensorHistoryRepositoryRemote.getAllLetterCodes()
        else sensorHistoryRepositoryLocal.getAllLetterCodes()
    }

    suspend fun getHistoryDataEntitySize(isRemote: Boolean): Int {
        return if (isRemote) sensorHistoryRepositoryRemote.getHistoryDataEntitySize()
        else sensorHistoryRepositoryLocal.getHistoryDataEntitySize()
    }

    suspend fun getGraphFirstCurveSensorHistoryList(
        limit: Int,
        offset: Int,
        isRemote: Boolean
    ): List<SensorHistoryDataEntity> {
        return if (isRemote) {
            sensorHistoryRepositoryRemote.getGraphFirstCurveSensorHistoryList(limit, offset)
        } else {
            sensorHistoryRepositoryLocal.getGraphFirstCurveSensorHistoryList(limit, offset)
        }
    }

    suspend fun getGraphSubsequentCurvesSensorHistoryList(
        sensorIndex: Int,
        fromDate: Date,
        toDate: Date,
        isRemote: Boolean
    ): List<SensorHistoryDataEntity> {
        return if (isRemote) {
            sensorHistoryRepositoryRemote.getGraphSubsequentCurvesSensorHistoryList(sensorIndex, fromDate, toDate)
        } else {
            sensorHistoryRepositoryLocal.getGraphSubsequentCurvesSensorHistoryList(sensorIndex, fromDate, toDate)
        }
    }

    fun invalidateDataSource(isRemote: Boolean) {
        if (isRemote) sensorHistoryRepositoryRemote.invalidateDataSource()
        else sensorHistoryRepositoryLocal.invalidateDataSource()
    }
}
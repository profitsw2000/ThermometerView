package ru.profitsw2000.graphtab.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.definition.indexKey
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.model.state.SensorHistoryDataLoadState
import ru.profitsw2000.data.model.state.filterscreen.SensorIdsLoadState
import java.util.Date

const val SENSOR_HISTORY_DATA_LOAD_SIZE = 48

class GraphViewModel(
    private val sensorHistoryInteractor: SensorHistoryInteractor,
    private val sensorHistoryMapper: SensorHistoryMapper,
    private val sensorHistoryGraphFilterRepository: SensorHistoryGraphFilterRepository
) : ViewModel() {
    //coroutine
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var lifecycleScope: CoroutineScope
    val selectedSensorIdsMutableList = mutableListOf<Long>()
    var offset = 0

    //LiveData
    private val _sensorHistoryListLiveData: MutableLiveData<SensorHistoryDataLoadState> =
        MutableLiveData<SensorHistoryDataLoadState>()
    val sensorHistoryListLiveData: LiveData<SensorHistoryDataLoadState> by this::_sensorHistoryListLiveData
    private val _sensorIdsListLiveData: MutableLiveData<>


    init {
        viewModelScope.launch {
            sensorHistoryGraphFilterRepository.sensorIdList = getSensorIdsList()
            loadInitData()
        }
    }

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.lifecycleScope = coroutineScope
    }

    private suspend fun loadInitData() {
        val graphData = getFilteredSensorsHistoryLists()
        if (graphData != null)
            _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Success(graphData)
        else
            _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Error("Database error.")
    }

    fun loadData(newItemsNumber: Int) {
        offset = if (offset + newItemsNumber < 0) 0
        else offset + newItemsNumber

        lifecycleScope.launch {
            val graphData = getFilteredSensorsHistoryLists()
            if (graphData != null)
                _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Success(graphData)
            else
                _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Error("Database error.")
        }
    }

    private suspend fun getFilteredSensorsHistoryLists(): List<List<SensorHistoryDataModel>>? {
        val firstSensorHistoryList = getFirstSensorHistoryList()
        val result = if (!firstSensorHistoryList.isNullOrEmpty()) {
            val beginDate = firstSensorHistoryList.first().date
            val endDate = firstSensorHistoryList.last().date
            val subsequentSensorsHistoryLists = getSubsequentHistoryLists(beginDate, endDate)

            if (subsequentSensorsHistoryLists.isNullOrEmpty()) null
            else listOf(firstSensorHistoryList) + subsequentSensorsHistoryLists
        } else null

        return result
    }

    private suspend fun getFirstSensorHistoryList(): List<SensorHistoryDataModel>? = withContext(Dispatchers.IO) {
        val deferred: Deferred<List<SensorHistoryDataModel>?> = ioCoroutineScope.async {
            try {
                val sensorHistoryDataList = sensorHistoryInteractor.getGraphFirstCurveSensorHistoryList(
                    filter = sensorHistoryGraphFilterRepository,
                    limit = SENSOR_HISTORY_DATA_LOAD_SIZE,
                    offset = offset,
                    false
                )
                sensorHistoryMapper.map(sensorHistoryDataList)
            } catch (exception: Exception) {
                null
            }
        }
        return@withContext deferred.await()
    }

    private suspend fun getSubsequentHistoryLists(
        fromDate: Date,
        toDate: Date
    ): List<List<SensorHistoryDataModel>>? = withContext(Dispatchers.IO) {
        runCatching {
            coroutineScope {
                val sensorsNumber = if (sensorHistoryGraphFilterRepository.sensorIdList.isNotEmpty())
                    sensorHistoryGraphFilterRepository.sensorIdList.size
                else sensorHistoryGraphFilterRepository.letterCodeList.size

                (1 until sensorsNumber).map { index ->
                    async {
                        val sensorHistoryDataList = sensorHistoryInteractor.getGraphSubsequentCurvesSensorHistoryList(
                            sensorIndex = index,
                            filter = sensorHistoryGraphFilterRepository,
                            fromDate = fromDate,
                            toDate = toDate,
                            isRemote = false
                        )
                        sensorHistoryMapper.map(sensorHistoryDataList)
                    }
                }.awaitAll()
            }
        }.getOrNull()
    }

    private suspend fun getSensorIdsList(): List<Long> {
        val deferred: Deferred<List<Long>> = ioCoroutineScope.async {
            try {
                sensorHistoryInteractor.getAllSensorIds(false)
            } catch (e: Exception) {
                e.printStackTrace()
                arrayListOf<Long>()
            }
        }
        return deferred.await()
    }

}
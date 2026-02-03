package ru.profitsw2000.graphtab.presentation.viewmodel

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
import kotlinx.coroutines.launch
import org.koin.core.definition.indexKey
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

    init {
        viewModelScope.launch {
            sensorHistoryGraphFilterRepository.sensorIdList = getSensorIdsList()
        }
        if (sensorHistoryGraphFilterRepository.sensorIdList.isNotEmpty())
            selectedSensorIdsMutableList.add(sensorHistoryGraphFilterRepository.sensorIdList[0])
        loadData(0)
    }

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.lifecycleScope = coroutineScope
    }

    fun loadData(newItemsNumber: Int) {
        offset = if (offset + newItemsNumber < 0) 0
        else offset + newItemsNumber

        lifecycleScope.launch {
            val graphData = getFilteredSensorsHistoryLists()
            if (graphData != null)
                _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Success(graphData)
            else
                _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Error(exc.message ?: "Unknown error.")

        }
    }

    private suspend fun getFilteredSensorsHistoryLists(): List<List<SensorHistoryDataModel>>? {
        val firstSensorHistoryList = getFirstSensorHistoryList()
        val result = if (firstSensorHistoryList != null) {
            val beginDate = firstSensorHistoryList.first().date
            val endDate = firstSensorHistoryList.last().date
            val subsequentSensorsHistoryLists = getSubsequentHistoryLists(beginDate, endDate)
            listOf(firstSensorHistoryList) + subsequentSensorsHistoryLists
        } else null

        return result
    }

    private suspend fun getFirstSensorHistoryList(): List<SensorHistoryDataModel>? {
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
        return deferred.await()
    }

    private suspend fun getSubsequentHistoryLists(fromDate: Date, toDate: Date): List<List<SensorHistoryDataModel>> {
        val sensorsNumber = if (sensorHistoryGraphFilterRepository.sensorIdList.isNotEmpty())
            sensorHistoryGraphFilterRepository.sensorIdList.size
        else sensorHistoryGraphFilterRepository.letterCodeList.size
        val deferredResults = (1 until sensorsNumber).map { index ->
            ioCoroutineScope.async {
                try {
                    val sensorHistoryDataList =
                        sensorHistoryInteractor.getGraphSubsequentCurvesSensorHistoryList(
                            sensorIndex = index,
                            filter = sensorHistoryGraphFilterRepository,
                            fromDate = fromDate,
                            toDate = toDate,
                            isRemote = false
                        )
                    sensorHistoryMapper.map(
                        sensorHistoryDataList
                    )
                } catch (exc: Exception) {
                    null
                }
            }
        }
        return deferredResults.awaitAll().filterNotNull()
    }

    private suspend fun getOneSensorHistoryListById(newItemsNumber: Int): List<SensorHistoryDataModel> {
        val deferred: Deferred<List<SensorHistoryDataModel>> = ioCoroutineScope.async {
            try {
                val sensorHistoryDataList = sensorHistoryInteractor.getSimpleSensorHistoryList(
                    sensorId = sensorHistoryGraphFilterRepository.sensorIdList[0],
                    limit = SENSOR_HISTORY_DATA_LOAD_SIZE,
                    offset = offset,
                    false
                )
                sensorHistoryMapper.map(sensorHistoryDataList)
            } catch (exc: Exception) {
                _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Error(exc.message ?: "Unknown error.")
                arrayListOf<SensorHistoryDataModel>()
            }
        }
        return deferred.await()
    }

    private suspend fun getMultipleSensorsHistoryDataListById(newItemsNumber: Int): List<List<SensorHistoryDataModel>> {
        val masterSensorHistoryDataList = getOneSensorHistoryDataListById(newItemsNumber)
        val begin = masterSensorHistoryDataList.last().date
        val end = masterSensorHistoryDataList.first().date
        val slaveSensorIdsList = sensorHistoryGraphFilterRepository.sensorIdList.drop(1)
        val deferredResults = slaveSensorIdsList.map { sensorId ->
            ioCoroutineScope.async {
                try {
                    val sensorHistoryDataList = sensorHistoryInteractor.getSimpleSensorHistoryList(
                        sensorId = sensorId,
                        limit = SENSOR_HISTORY_DATA_LOAD_SIZE,
                        offset = offset,
                        false
                    )
                    sensorHistoryMapper.map(
                        sensorHistoryDataList
                    )
                } catch (exc: Exception) {
                    _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Error(exc.message ?: "Unknown error.")
                    arrayListOf<SensorHistoryDataModel>()
                }
            }
        }
        return deferredResults.awaitAll()

/*        val deferred: Deferred<SensorHistoryDataLoadState> = ioCoroutineScope.async {
            try {
                val sensorHistoryDataList = sensorHistoryInteractor.getSimpleSensorHistoryList(
                    sensorId = 0x28FF5CCAC11704C5,
                    limit = SENSOR_HISTORY_DATA_LOAD_SIZE,
                    offset = offset,
                    false
                )
                SensorHistoryDataLoadState.Success(
                    sensorHistoryMapper.map(
                        sensorHistoryDataList
                    )
                )
            } catch (exc: Exception) {
                SensorHistoryDataLoadState.Error(exc.message ?: "Unknown error.")
            }
        }
        return deferred.await()*/
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
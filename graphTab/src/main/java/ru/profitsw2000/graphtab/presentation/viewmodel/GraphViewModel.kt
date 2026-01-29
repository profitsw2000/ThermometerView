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
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.model.state.SensorHistoryDataLoadState
import ru.profitsw2000.data.model.state.filterscreen.SensorIdsLoadState

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
    val string = "str" + 2 + 2

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
            //_sensorHistoryListLiveData.value = getSensorHistoryDataList(newItemsNumber)
        }
    }

    private suspend fun getOneSensorHistoryDataListById(newItemsNumber: Int): List<SensorHistoryDataModel> {
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
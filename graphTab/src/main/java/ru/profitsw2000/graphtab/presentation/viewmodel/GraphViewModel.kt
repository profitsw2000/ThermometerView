package ru.profitsw2000.graphtab.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.model.state.SensorHistoryDataLoadState

class GraphViewModel(
    private val sensorHistoryInteractor: SensorHistoryInteractor,
    private val sensorHistoryMapper: SensorHistoryMapper
) {
    //coroutine
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var lifecycleScope: CoroutineScope

    //LiveData
    private val _sensorHistoryListLiveData: MutableLiveData<SensorHistoryDataLoadState> =
        MutableLiveData<SensorHistoryDataLoadState>()
    val sensorHistoryListLiveData: LiveData<SensorHistoryDataLoadState> by this::_sensorHistoryListLiveData

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.lifecycleScope = coroutineScope
    }

    fun loadData() {
        lifecycleScope.launch {
            _sensorHistoryListLiveData.value = getSimpleSensorHistoryDataList()
        }
    }
    private suspend fun getSimpleSensorHistoryDataList(): SensorHistoryDataLoadState {
        val deferred: Deferred<SensorHistoryDataLoadState> = ioCoroutineScope.async {
            try {
                val sensorHistoryDataList = sensorHistoryInteractor.getSimpleSensorHistoryList(
                    sensorId = 0x28FF5CCAC11704C5,
                    limit = 30,
                    offset = 0,
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
        return deferred.await()
    }

}
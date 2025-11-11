package ru.profitsw2000.tabletab.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.model.state.filterscreen.LetterCodesLoadState
import ru.profitsw2000.data.model.state.filterscreen.LocalIdsLoadState
import ru.profitsw2000.data.model.state.filterscreen.SensorIdsLoadState

class FilterViewModel(
    private val sensorHistoryInteractor: SensorHistoryInteractor
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var _sensorIdsLoadLiveData: MutableLiveData<SensorIdsLoadState>
    val sensorIdsLoadLiveData: LiveData<SensorIdsLoadState> by this::_sensorIdsLoadLiveData

    private lateinit var _localIdsLoadLiveData: MutableLiveData<LocalIdsLoadState>
    val localIdsLoadLiveData: LiveData<LocalIdsLoadState> by this::_localIdsLoadLiveData

    private lateinit var _letterCodesLoadLiveData: MutableLiveData<LetterCodesLoadState>
    val letterCodesLoadLiveData: LiveData<LetterCodesLoadState> by this::_letterCodesLoadLiveData

    fun loadFilterElements() {
        _sensorIdsLoadLiveData.value = SensorIdsLoadState.Loading
        _localIdsLoadLiveData.value = LocalIdsLoadState.Loading
        _letterCodesLoadLiveData.value = LetterCodesLoadState.Loading
        viewModelScope.launch {
            _sensorIdsLoadLiveData.value = querySensorIdList()
            _localIdsLoadLiveData.value = queryLocalIdList()
            _letterCodesLoadLiveData.value = queryLetterCodesList()
        }
    }

    private suspend fun querySensorIdList(): SensorIdsLoadState {
        val deferred: Deferred<SensorIdsLoadState> = coroutineScope.async {
            try {
                val sensorIdsList = sensorHistoryInteractor.getAllSensorIds(false)
                SensorIdsLoadState.Success(sensorIdsList)
            } catch (e: Exception) {
                e.printStackTrace()
                SensorIdsLoadState.Error
            }
        }
        return deferred.await()
    }

    private suspend fun queryLocalIdList(): LocalIdsLoadState {
        val deferred: Deferred<LocalIdsLoadState> = coroutineScope.async {
            try {
                val localIdsList = sensorHistoryInteractor.getAllSensorLocalIds(false)
                LocalIdsLoadState.Success(localIdsList)
            } catch (e: Exception) {
                e.printStackTrace()
                LocalIdsLoadState.Error
            }
        }
        return deferred.await()
    }

    private suspend fun queryLetterCodesList(): LetterCodesLoadState {
        val deferred: Deferred<LetterCodesLoadState> = coroutineScope.async {
            try {
                val letterCodesList = sensorHistoryInteractor.getAllLetterCodes(false)
                LetterCodesLoadState.Success(letterCodesList)
            } catch (e: Exception) {
                e.printStackTrace()
                LetterCodesLoadState.Error
            }
        }
        return deferred.await()
    }
}
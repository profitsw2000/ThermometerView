package ru.profitsw2000.tabletab.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.model.SensorHistoryDataModel

class TableViewModel(
    private val sensorHistoryInteractor: SensorHistoryInteractor
) : ViewModel() {

    val historyListPagedData: LiveData<PagingData<SensorHistoryDataModel>> =
        sensorHistoryInteractor
            .getHistoryPagedData(false)
            .cachedIn(viewModelScope)

}
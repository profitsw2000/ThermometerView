package ru.profitsw2000.data.domain

import kotlinx.coroutines.flow.StateFlow

interface DateTimeRepository {

    val dateTimeDataString: StateFlow<String>
    val dataExchangeStartSignal: StateFlow<Boolean>

    fun getCurrentDateTimeArray(): Array<Int>
}
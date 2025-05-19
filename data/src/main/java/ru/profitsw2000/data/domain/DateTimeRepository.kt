package ru.profitsw2000.data.domain

import kotlinx.coroutines.flow.StateFlow

interface DateTimeRepository {

    val dateDataString: StateFlow<String>
    val timeDataString: StateFlow<String>
    val dataExchangeStartSignal: StateFlow<Boolean>

    fun getCurrentDateTimeArray(): Array<Int>
}